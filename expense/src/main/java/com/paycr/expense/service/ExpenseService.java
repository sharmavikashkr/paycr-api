package com.paycr.expense.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.paycr.common.awss3.AwsS3Folder;
import com.paycr.common.awss3.AwsS3Service;
import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpenseAttachment;
import com.paycr.common.data.domain.ExpenseNote;
import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.ExpensePaymentRepository;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.type.NoteType;
import com.paycr.common.type.ObjectType;
import com.paycr.common.type.PayType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.PaycrUtil;
import com.paycr.expense.validation.ExpenseNoteValidator;

@Service
public class ExpenseService {

	private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

	private int maxUploadSizeInMb = 2 * 1024 * 1024;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ExpenseRepository expRepo;

	@Autowired
	private ExpensePaymentRepository payRepo;

	@Autowired
	private ExpenseNoteValidator noteValid;

	@Autowired
	private Server server;

	@Autowired
	private AwsS3Service awsS3Ser;

	@Autowired
	private TimelineService tlService;

	public Expense getExpense(String expenseCode) {
		return expRepo.findByExpenseCode(expenseCode);
	}

	public void delete(String expenseCode) {
		logger.info("Delete Expense : {}", expenseCode);
		Date timeNow = new Date();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Expense expense = expRepo.findByExpenseCodeAndMerchant(expenseCode, merchant);
		if (CommonUtil.isNotNull(expense.getNote())) {
			expense.getNote().setDeleted(true);
		}
		List<ExpensePayment> expPays = payRepo.findByExpenseCode(expense.getExpenseCode());
		for (ExpensePayment expPay : expPays) {
			expPay.setDeleted(true);
		}
		if (CommonUtil.isNotEmpty(expPays)) {
			payRepo.saveAll(expPays);
		}
		expense.setDeleted(true);
		expense.setUpdated(timeNow);
		expRepo.save(expense);
		tlService.saveToTimeline(expense.getId(), ObjectType.INVOICE, "Expense deleted", true, user.getEmail());
	}

	public void refund(BigDecimal amount, String expenseCode) {
		logger.info("Refund Expense : {} with amount : {}", expenseCode, amount);
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Expense expense = expRepo.findByExpenseCodeAndMerchant(expenseCode, merchant);
		if (!ExpenseStatus.PAID.equals(expense.getStatus())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Refund Not allowed");
		}
		List<ExpensePayment> refunds = payRepo.findByExpenseCodeAndPayType(expense.getExpenseCode(), PayType.REFUND);
		BigDecimal refundAllowed = expense.getPayAmount();
		for (ExpensePayment refund : refunds) {
			if ("refund".equalsIgnoreCase(refund.getStatus())) {
				refundAllowed = refundAllowed.subtract(refund.getAmount());
			}
		}
		if (ExpenseStatus.PAID.equals(expense.getStatus())
				&& refundAllowed.setScale(2, RoundingMode.HALF_DOWN).compareTo(amount) >= 0) {
			Date timeNow = new Date();
			ExpensePayment payment = expense.getPayment();
			ExpensePayment refPay = new ExpensePayment();
			refPay.setAmount(amount);
			refPay.setCreated(timeNow);
			refPay.setPaidDate(timeNow);
			refPay.setExpenseCode(expense.getExpenseCode());
			refPay.setMerchant(merchant);
			refPay.setPaymentRefNo(payment.getPaymentRefNo());
			refPay.setStatus("refund");
			refPay.setPayMode(payment.getPayMode());
			refPay.setMethod(payment.getMethod());
			refPay.setPayType(PayType.REFUND);
			payRepo.save(refPay);
			tlService.saveToTimeline(expense.getId(), ObjectType.EXPENSE, "Expense refunded with amount : " + amount,
					true, user.getEmail());
		} else {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Refund Not allowed");
		}
	}

	public void markPaid(ExpensePayment payment) {
		logger.info("Mark paid Expense with payment : {}", new Gson().toJson(payment));
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Expense expense = expRepo.findByExpenseCodeAndMerchant(payment.getExpenseCode(), merchant);
		if (ExpenseStatus.PAID.equals(expense.getStatus())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Mark paid Not allowed");
		}
		Date timeNow = new Date();
		payment.setCreated(timeNow);
		payment.setStatus("captured");
		payment.setAmount(expense.getPayAmount());
		payment.setPayType(PayType.SALE);
		payment.setExpenseCode(expense.getExpenseCode());
		payment.setMerchant(merchant);
		expense.setPayment(payment);
		expense.setStatus(ExpenseStatus.PAID);
		expRepo.save(expense);
		tlService.saveToTimeline(expense.getId(), ObjectType.EXPENSE, "Expense marked paid", true, user.getEmail());
	}

	public void newNote(ExpenseNote note) {
		logger.info("New ExpenseNote request : {}", new Gson().toJson(note));
		Date timeNow = new Date();
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		Expense expense = expRepo.findByExpenseCode(note.getExpenseCode());
		note.setCreated(timeNow);
		note.setCreatedBy(user.getEmail());
		note.setMerchant(merchant);
		noteValid.validate(note);
		if (NoteType.DEBIT.equals(note.getNoteType())) {
			refund(note.getPayAmount(), expense.getExpenseCode());
		}
		expense.setNote(note);
		expRepo.save(expense);
	}

	public void saveAttach(String expenseCode, MultipartFile attach) throws IOException {
		logger.info("New Expense : {} attachment : {}", expenseCode, attach.getName());
		if (maxUploadSizeInMb < attach.getSize()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Banner size limit 2MBs");
		}
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		Expense expense = getExpense(expenseCode);
		List<ExpenseAttachment> attachments = expense.getAttachments();
		if (CommonUtil.isNull(attachments)) {
			attachments = new ArrayList<>();
		}
		if (attachments.size() >= 5) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Max 5 attachments allowed");
		}
		String attachName = expenseCode + "-" + attach.getOriginalFilename();
		File file = new File(server.getExpAttachLocation() + attachName);
		PaycrUtil.saveFile(file, attach);
		awsS3Ser.saveFile(merchant.getAccessKey().concat("/").concat(AwsS3Folder.INV_ATTACH), file);
		ExpenseAttachment attachment = new ExpenseAttachment();
		attachment.setName(attach.getOriginalFilename());
		attachment.setCreated(new Date());
		attachment.setCreatedBy(user.getEmail());
		attachment.setExpense(expense);
		attachments.add(attachment);
		expRepo.save(expense);

		tlService.saveToTimeline(expense.getId(), ObjectType.EXPENSE,
				"Attachment saved : " + attach.getOriginalFilename(), true, user.getEmail());
	}

	public byte[] getAttach(String accessKey, String expenseCode, String attachName) throws IOException {
		logger.info("Download Expense attachment : {} by : {}", attachName, accessKey);
		attachName = expenseCode + "-" + attachName;
		return awsS3Ser.getFile(accessKey.concat("/").concat(AwsS3Folder.EXP_ATTACH), attachName);
	}

	public List<ExpensePayment> payments(String expenseCode) {
		logger.info("All Expense payments : {}", expenseCode);
		return payRepo.findByExpenseCode(expenseCode);
	}

}
