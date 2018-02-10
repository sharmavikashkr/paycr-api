package com.paycr.expense.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import com.paycr.expense.validation.ExpenseNoteValidator;

@Service
public class ExpenseService {

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
	private TimelineService tlService;

	public Expense getExpense(String expenseCode) {
		return expRepo.findByExpenseCode(expenseCode);
	}

	public void refund(BigDecimal amount, String expenseCode) {
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
				&& refundAllowed.setScale(2, BigDecimal.ROUND_HALF_DOWN).compareTo(amount) >= 0) {
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
		if (maxUploadSizeInMb < attach.getSize()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Banner size limit 2MBs");
		}
		PcUser user = secSer.findLoggedInUser();
		Expense expense = getExpense(expenseCode);
		List<ExpenseAttachment> attachments = expense.getAttachments();
		if (CommonUtil.isNull(attachments)) {
			attachments = new ArrayList<>();
		}
		if (attachments.size() >= 5) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Max 5 attachments allowed");
		}
		ExpenseAttachment attachment = new ExpenseAttachment();
		attachment.setName(attach.getOriginalFilename());
		attachment.setCreated(new Date());
		attachment.setCreatedBy(user.getEmail());
		attachment.setExpense(expense);
		attachments.add(attachment);
		expRepo.save(expense);
		String attachName = expenseCode + "-" + attach.getOriginalFilename();
		File file = null;
		file = new File(server.getMerchantLocation() + "attachment/" + attachName);
		FileOutputStream out = new FileOutputStream(file);
		out.write(attach.getBytes());
		out.close();
		tlService.saveToTimeline(expense.getId(), ObjectType.EXPENSE,
				"Attachment saved : " + attach.getOriginalFilename(), true, user.getEmail());
	}

	public byte[] getAttach(String expenseCode, String attachName) throws IOException {
		attachName = expenseCode + "-" + attachName;
		Path path = Paths.get(server.getMerchantLocation() + "attachment/" + attachName);
		return Files.readAllBytes(path);
	}

	public List<ExpensePayment> payments(String expenseCode) {
		return payRepo.findByExpenseCode(expenseCode);
	}

}
