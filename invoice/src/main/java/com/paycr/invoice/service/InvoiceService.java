package com.paycr.invoice.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.Server;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.BulkCategory;
import com.paycr.common.data.domain.BulkInvoiceUpload;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceAttachment;
import com.paycr.common.data.domain.InvoiceCreditNote;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.data.repository.BulkCategoryRepository;
import com.paycr.common.data.repository.BulkInvoiceUploadRepository;
import com.paycr.common.data.repository.InvoicePaymentRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.RecurringInvoiceRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ObjectType;
import com.paycr.common.type.PayType;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;
import com.paycr.invoice.helper.InvoiceHelper;
import com.paycr.invoice.scheduler.InvoiceSchedulerService;
import com.paycr.invoice.validation.CreditNoteValidator;
import com.razorpay.RazorpayException;

@Service
public class InvoiceService {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

	@Autowired
	private SecurityService secSer;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private InvoicePaymentRepository payRepo;

	@Autowired
	private CreditNoteValidator creditNoteValid;

	@Autowired
	private InvoiceHelper invHelp;

	@Autowired
	private NotifyService<InvoiceNotify> invNotSer;

	@Autowired
	private PaymentService payService;

	@Autowired
	private Server server;

	@Autowired
	private InvoiceSchedulerService invSchSer;

	@Autowired
	private RecurringInvoiceRepository recInvRepo;

	@Autowired
	private BulkInvoiceUploadRepository bulkUpdRepo;

	@Autowired
	private BulkCategoryRepository bulkCatRepo;

	@Autowired
	private TimelineService tlService;

	private final ExecutorService exec = Executors.newFixedThreadPool(5);

	public Invoice getInvoice(String invoiceCode) {
		return invRepo.findByInvoiceCode(invoiceCode);
	}

	public void expire(String invoiceCode) {
		Date timeNow = new Date();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant);
		if (invoice.isNeverExpire()) {
			throw new PaycrException(Constants.FAILURE, "Cannot be expired");
		}
		if (timeNow.compareTo(invoice.getExpiry()) < 0 && !InvoiceStatus.PAID.equals(invoice.getStatus())) {
			invoice.setExpiry(timeNow);
			invoice.setStatus(InvoiceStatus.EXPIRED);
			invRepo.save(invoice);
		}
		if (InvoiceType.RECURRING.equals(invoice.getInvoiceType())) {
			RecurringInvoice recInv = recInvRepo.findByInvoiceAndActive(invoice, true);
			if (recInv != null) {
				recInv.setActive(false);
				recInvRepo.save(recInv);
			}
		}
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Invoice expired", true, user.getEmail());
	}

	public void notify(String invoiceCode, InvoiceNotify notify) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant);
		if (InvoiceType.SINGLE.equals(invoice.getInvoiceType()) || !InvoiceStatus.PAID.equals(invoice.getStatus())
				&& !InvoiceStatus.EXPIRED.equals(invoice.getStatus())) {
			notify.setCreated(new Date());
			notify.setInvoice(invoice);
			invNotSer.notify(notify);
			invoice.getNotices().add(notify);
			if (InvoiceStatus.CREATED.equals(invoice.getStatus())) {
				invoice.setStatus(InvoiceStatus.UNPAID);
			}
			invRepo.save(invoice);
			tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Notification sent to consumer", true,
					user.getEmail());
		} else {
			throw new PaycrException(Constants.FAILURE, "Notify Not allowed");
		}
	}

	public void enquire(String invoiceCode) throws RazorpayException {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant);
		if (InvoiceType.SINGLE.equals(invoice.getInvoiceType()) && !InvoiceStatus.PAID.equals(invoice.getStatus())
				&& invoice.getPayment() != null) {
			payService.enquire(invoice);
		} else {
			throw new PaycrException(Constants.FAILURE, "Enquiry Not allowed");
		}
	}

	public InvoicePayment refund(BigDecimal amount, String invoiceCode) throws RazorpayException {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant);
		if (!InvoiceType.SINGLE.equals(invoice.getInvoiceType()) || !InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "Refund Not allowed");
		}
		List<InvoicePayment> refunds = payRepo.findByInvoiceCodeAndPayType(invoice.getInvoiceCode(), PayType.REFUND);
		BigDecimal refundAllowed = invoice.getPayAmount();
		for (InvoicePayment refund : refunds) {
			if ("refund".equalsIgnoreCase(refund.getStatus())) {
				refundAllowed = refundAllowed.subtract(refund.getAmount());
			}
		}
		if (InvoiceStatus.PAID.equals(invoice.getStatus()) && refundAllowed.compareTo(amount) >= 0) {
			return payService.refund(invoice, amount, user.getEmail());
		} else {
			throw new PaycrException(Constants.FAILURE, "Refund Not allowed");
		}
	}

	public void newCreditNote(InvoiceCreditNote creditNote) {
		Date timeNow = new Date();
		try {
			PcUser user = secSer.findLoggedInUser();
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			Invoice invoice = invRepo.findByInvoiceCode(creditNote.getInvoiceCode());
			creditNote.setCreated(timeNow);
			creditNote.setCreatedBy(user.getEmail());
			creditNote.setMerchant(merchant);
			creditNoteValid.validate(creditNote);
			InvoicePayment refPay = refund(creditNote.getPayAmount(), invoice.getInvoiceCode());
			creditNote.setPayment(refPay);
			invoice.setCreditNote(creditNote);
			invRepo.save(invoice);
		} catch (Exception e) {
			logger.error("Create credit note failed for invoice : {} ", creditNote.getInvoiceCode(), e);
			throw new PaycrException(HttpStatus.BAD_REQUEST_400, e.getMessage());
		}
	}

	public void markPaid(InvoicePayment payment) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(payment.getInvoiceCode(), merchant);
		if (!InvoiceType.SINGLE.equals(invoice.getInvoiceType()) || InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "Mark paid Not allowed");
		}
		Date timeNow = new Date();
		payment.setCreated(timeNow);
		payment.setPaidOn(payment.getPaidOn());
		payment.setAmount(invoice.getPayAmount());
		payment.setStatus("captured");
		payment.setPayType(PayType.SALE);
		payment.setInvoiceCode(invoice.getInvoiceCode());
		payment.setMerchant(merchant);
		invoice.setPayment(payment);
		invoice.setStatus(InvoiceStatus.PAID);
		invRepo.save(invoice);
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Invoice marked paid", true, user.getEmail());
	}

	public void saveAttach(String invoiceCode, MultipartFile attach) throws IOException {
		PcUser user = secSer.findLoggedInUser();
		Invoice invoice = getInvoice(invoiceCode);
		List<InvoiceAttachment> attachments = invoice.getAttachments();
		if (attachments == null) {
			attachments = new ArrayList<>();
		}
		if (attachments.size() >= 5) {
			throw new PaycrException(Constants.FAILURE, "Max 5 attachments allowed");
		}
		InvoiceAttachment attachment = new InvoiceAttachment();
		attachment.setName(attach.getOriginalFilename());
		attachment.setCreated(new Date());
		attachment.setCreatedBy(user.getEmail());
		attachment.setInvoice(invoice);
		attachments.add(attachment);
		invRepo.save(invoice);
		String attachName = invoiceCode + "-" + attach.getOriginalFilename();
		File file = null;
		file = new File(server.getMerchantLocation() + "attachment/" + attachName);
		FileOutputStream out = new FileOutputStream(file);
		out.write(attach.getBytes());
		out.close();
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE,
				"Attachment saved : " + attach.getOriginalFilename(), true, user.getEmail());
	}

	public byte[] getAttach(String invoiceCode, String attachName) throws IOException {
		attachName = invoiceCode + "-" + attachName;
		Path path = Paths.get(server.getMerchantLocation() + "attachment/" + attachName);
		return Files.readAllBytes(path);
	}

	@Transactional
	public void recurr(String invoiceCode, RecurringInvoice recInv, String createdBy) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (invoice == null || !InvoiceType.RECURRING.equals(invoice.getInvoiceType())
				|| InvoiceStatus.EXPIRED.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "Invalid invoice");
		}
		if (recInv.getStartDate() == null) {
			throw new PaycrException(Constants.FAILURE, "Invalid start date");
		}
		recInv.setActive(true);
		recInv.setInvoice(invoice);
		recInv.setRemaining(recInv.getTotal());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.getStartOfDay(recInv.getStartDate()));
		calendar.set(Calendar.HOUR_OF_DAY, 22);
		calendar.set(Calendar.MINUTE, 0);
		recInv.setNextDate(calendar.getTime());
		recInv.setStartDate(calendar.getTime());

		Date timeNow = new Date();
		Date start = DateUtil.getStartOfDay(timeNow);
		Date end = DateUtil.getEndOfDay(timeNow);
		if (start.after(recInv.getStartDate())) {
			throw new PaycrException(Constants.FAILURE, "Cannot schedule from older date");
		}
		RecurringInvoice ext = recInvRepo.findByInvoiceAndActive(invoice, true);
		if (ext != null) {
			ext.setActive(false);
			recInvRepo.save(ext);
		}
		recInvRepo.save(recInv);
		if (start.before(recInv.getStartDate()) && end.after(recInv.getStartDate())) {
			Invoice childInvoice = invHelp.prepareChildInvoice(invoice.getInvoiceCode(), InvoiceType.SINGLE, createdBy);
			exec.execute(invSchSer.processInvoice(recInv, childInvoice));
		}
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Recurr setting added", true, createdBy);
	}

	public List<RecurringInvoice> allRecurr(String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		return recInvRepo.findByInvoice(invoice);
	}

	public List<BulkInvoiceUpload> getUploads(String invoiceCode) {
		return bulkUpdRepo.findByInvoiceCode(invoiceCode);
	}

	public byte[] downloadFile(String filename) throws IOException {
		Path path = Paths.get(server.getBulkInvoiceLocation() + filename);
		return Files.readAllBytes(path);
	}

	public List<InvoicePayment> payments(String invoiceCode) {
		return payRepo.findByInvoiceCode(invoiceCode);
	}

	public List<BulkCategory> getCategories(String invoiceCode) {
		return bulkCatRepo.findByInvoiceCode(invoiceCode);
	}

}
