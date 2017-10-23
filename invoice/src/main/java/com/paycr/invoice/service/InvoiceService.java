package com.paycr.invoice.service;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.Attachment;
import com.paycr.common.data.domain.BulkCategory;
import com.paycr.common.data.domain.BulkUpload;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.data.domain.Timeline;
import com.paycr.common.data.repository.BulkCategoryRepository;
import com.paycr.common.data.repository.BulkUploadRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.RecurringInvoiceRepository;
import com.paycr.common.data.repository.TimelineRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.NotifyService;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ObjectType;
import com.paycr.common.type.PayType;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;
import com.paycr.invoice.helper.InvoiceHelper;
import com.paycr.invoice.scheduler.InvoiceSchedulerService;
import com.razorpay.RazorpayException;

@Service
public class InvoiceService {
	
	@Autowired
	private SecurityService secSer;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private PaymentRepository payRepo;

	@Autowired
	private InvoiceHelper invHelp;

	@Autowired
	private NotifyService notSer;

	@Autowired
	private PaymentService payService;

	@Autowired
	private Server server;

	@Autowired
	private InvoiceSchedulerService invSchSer;

	@Autowired
	private RecurringInvoiceRepository recInvRepo;

	@Autowired
	private BulkUploadRepository bulkUpdRepo;
	
	@Autowired
	private BulkCategoryRepository bulkCatRepo;

	@Autowired
	private TimelineRepository tlRepo;

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
		Timeline tl = new Timeline();
		tl.setCreatedBy(user.getEmail());
		tl.setCreated(timeNow);
		tl.setInternal(true);
		tl.setMessage("Invoice Expired");
		tl.setObjectId(invoice.getId());
		tl.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tl);
	}

	public void notify(String invoiceCode, InvoiceNotify invoiceNotify) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant);
		if (InvoiceType.SINGLE.equals(invoice.getInvoiceType()) || !InvoiceStatus.PAID.equals(invoice.getStatus())
				&& !InvoiceStatus.EXPIRED.equals(invoice.getStatus())) {
			notSer.notify(invoice, invoiceNotify);
			invoiceNotify.setCreated(new Date());
			invoiceNotify.setInvoice(invoice);
			invoice.getInvoiceNotices().add(invoiceNotify);
			if (InvoiceStatus.CREATED.equals(invoice.getStatus())) {
				invoice.setStatus(InvoiceStatus.UNPAID);
			}
			invRepo.save(invoice);
			Timeline tl = new Timeline();
			tl.setCreatedBy(user.getEmail());
			tl.setCreated(new Date());
			tl.setInternal(true);
			tl.setMessage("Notification sent to consumer");
			tl.setObjectId(invoice.getId());
			tl.setObjectType(ObjectType.INVOICE);
			tlRepo.save(tl);
		} else {
			throw new PaycrException(Constants.FAILURE, "Notify Not allowed");
		}
	}

	public void enquire(String invoiceCode) throws RazorpayException {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant);
		if (InvoiceType.SINGLE.equals(invoice.getInvoiceType()) && !InvoiceStatus.PAID.equals(invoice.getStatus())) {
			payService.enquire(invoice);
		} else {
			throw new PaycrException(Constants.FAILURE, "Enquiry Not allowed");
		}
	}

	public void refund(BigDecimal amount, String invoiceCode) throws RazorpayException {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant);
		if (!InvoiceType.SINGLE.equals(invoice.getInvoiceType()) || !InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "Refund Not allowed");
		}
		List<Payment> refunds = payRepo.findByInvoiceCodeAndPayType(invoice.getInvoiceCode(), PayType.REFUND);
		BigDecimal refundAllowed = invoice.getPayAmount();
		for (Payment refund : refunds) {
			if ("refund".equalsIgnoreCase(refund.getStatus())) {
				refundAllowed = refundAllowed.subtract(refund.getAmount());
			}
		}
		if (InvoiceStatus.PAID.equals(invoice.getStatus()) && refundAllowed.compareTo(amount) >= 0) {
			payService.refund(invoice, amount, user.getEmail());
		} else {
			throw new PaycrException(Constants.FAILURE, "Refund Not allowed");
		}
	}

	public void markPaid(Payment payment) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(payment.getInvoiceCode(), merchant);
		if (!InvoiceType.SINGLE.equals(invoice.getInvoiceType()) || InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "Mark paid Not allowed");
		}
		Date timeNow = new Date();
		payment.setCreated(timeNow);
		payment.setStatus("captured");
		payment.setPayType(PayType.SALE);
		payment.setInvoiceCode(invoice.getInvoiceCode());
		payment.setMerchant(merchant);
		invoice.setPayment(payment);
		invoice.setStatus(InvoiceStatus.PAID);
		invRepo.save(invoice);
		Timeline tl = new Timeline();
		tl.setCreatedBy(user.getEmail());
		tl.setCreated(timeNow);
		tl.setInternal(true);
		tl.setMessage("Invoice marked paid");
		tl.setObjectId(invoice.getId());
		tl.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tl);
	}

	public void saveAttach(String invoiceCode, MultipartFile attach) throws IOException {
		PcUser user = secSer.findLoggedInUser();
		Invoice invoice = getInvoice(invoiceCode);
		List<Attachment> attachments = invoice.getAttachments();
		if (attachments == null) {
			attachments = new ArrayList<>();
		}
		if (attachments.size() >= 5) {
			throw new PaycrException(Constants.FAILURE, "Max 5 attachments allowed");
		}
		Attachment attachment = new Attachment();
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
		Timeline tl = new Timeline();
		tl.setCreatedBy(user.getEmail());
		tl.setCreated(new Date());
		tl.setInternal(true);
		tl.setMessage("Attachment saved : " + attach.getOriginalFilename());
		tl.setObjectId(invoice.getId());
		tl.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tl);
	}

	public byte[] getAttach(String invoiceCode, String attachName) throws IOException {
		attachName = invoiceCode + "-" + attachName;
		Path path = Paths.get(server.getMerchantLocation() + "attachment/" + attachName);
		return Files.readAllBytes(path);
	}

	public void recurr(String invoiceCode, RecurringInvoice recInv, String createdBy) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (invoice == null || !InvoiceType.RECURRING.equals(invoice.getInvoiceType())
				|| InvoiceStatus.EXPIRED.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "Invalid invoice");
		}
		RecurringInvoice ext = recInvRepo.findByInvoiceAndActive(invoice, true);
		if (ext != null) {
			ext.setActive(false);
			recInvRepo.save(ext);
		}
		Date timeNow = new Date();
		Date start = DateUtil.getStartOfDay(timeNow);
		Date end = DateUtil.getEndOfDay(timeNow);
		if (recInv.getStartDate() == null || start.after(recInv.getStartDate())) {
			throw new PaycrException(Constants.FAILURE, "Invalid start date");
		}
		recInv.setActive(true);
		recInv.setInvoice(invoice);
		recInv.setRemaining(recInv.getTotal());
		recInv.setNextDate(recInv.getStartDate());
		recInvRepo.save(recInv);
		if (start.before(recInv.getStartDate()) && end.after(recInv.getStartDate())) {
			Invoice childInvoice = invHelp.prepareChildInvoice(invoice.getInvoiceCode(), InvoiceType.SINGLE, createdBy);
			Thread th = new Thread(invSchSer.processInvoice(recInv, childInvoice, timeNow));
			th.start();
		}
		Timeline tlParent = new Timeline();
		tlParent.setCreatedBy(createdBy);
		tlParent.setCreated(timeNow);
		tlParent.setInternal(true);
		tlParent.setMessage("Recurr setting added");
		tlParent.setObjectId(invoice.getId());
		tlParent.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tlParent);
	}

	public List<RecurringInvoice> allRecurr(String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		return recInvRepo.findByInvoice(invoice);
	}

	public List<BulkUpload> getUploads(String invoiceCode) {
		return bulkUpdRepo.findByInvoiceCode(invoiceCode);
	}

	public byte[] downloadFile(String filename) throws IOException {
		Path path = Paths.get(server.getBulkCsvLocation() + filename);
		return Files.readAllBytes(path);
	}

	public List<Payment> payments(String invoiceCode) {
		return payRepo.findByInvoiceCode(invoiceCode);
	}

	public List<BulkCategory> getCategories(String invoiceCode) {
		return bulkCatRepo.findByInvoiceCode(invoiceCode);
	}

}
