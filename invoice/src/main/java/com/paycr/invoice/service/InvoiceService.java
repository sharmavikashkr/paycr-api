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
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.RecurringInvoiceRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.NotifyService;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.PayType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;
import com.paycr.invoice.helper.InvoiceHelper;
import com.paycr.invoice.scheduler.InvoiceSchedulerService;
import com.paycr.invoice.validation.InvoiceValidator;
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
	private MerchantPricingRepository merPriRepo;

	@Autowired
	private InvoiceHelper invHelp;

	@Autowired
	private NotifyService notSer;

	@Autowired
	private InvoiceValidator invValidator;

	@Autowired
	private PaymentService payService;

	@Autowired
	private Server server;

	@Autowired
	private InvoiceSchedulerService invSchSer;

	@Autowired
	private RecurringInvoiceRepository recInvRepo;

	public Invoice single(Invoice invoice) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		invoice.setMerchant(merchant);
		invoice.setCreatedBy(user.getEmail());
		invValidator.validate(invoice);
		invRepo.save(invoice);
		if (!invoice.isUpdate()) {
			MerchantPricing merPri = invoice.getMerchantPricing();
			merPri.setInvCount(merPri.getInvCount() + 1);
			merPriRepo.save(merPri);
		}
		return invoice;
	}

	public Invoice getInvoice(String invoiceCode) {
		return invRepo.findByInvoiceCode(invoiceCode);
	}

	public void expire(String invoiceCode) {
		Date timeNow = new Date();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant);
		if (timeNow.compareTo(invoice.getExpiry()) < 0 && !InvoiceStatus.PAID.equals(invoice.getStatus())) {
			invoice.setExpiry(timeNow);
			invoice.setStatus(InvoiceStatus.EXPIRED);
			invRepo.save(invoice);
		}
	}

	public void notify(String invoiceCode, InvoiceNotify invoiceNotify) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant);
		if (InvoiceType.SINGLE.equals(invoice.getInvoiceType()) || !InvoiceStatus.PAID.equals(invoice.getStatus())
				&& !InvoiceStatus.EXPIRED.equals(invoice.getStatus())) {
			notSer.notify(invoice, invoiceNotify);
			invoiceNotify.setCreated(new Date());
			invoiceNotify.setInvoice(invoice);
			invoice.getInvoiceNotices().add(invoiceNotify);
			invRepo.save(invoice);
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
			payService.refund(invoice, amount);
		} else {
			throw new PaycrException(Constants.FAILURE, "Refund Not allowed");
		}
	}

	public void markPaid(Payment payment) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(payment.getInvoiceCode(), merchant);
		if (!InvoiceType.SINGLE.equals(invoice.getInvoiceType()) || InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "Mark paid Not allowed");
		}
		payment.setCreated(new Date());
		payment.setStatus("captured");
		payment.setPayType(PayType.SALE);
		payment.setInvoiceCode(invoice.getInvoiceCode());
		payment.setMerchant(merchant);
		invoice.setPayment(payment);
		invoice.setStatus(InvoiceStatus.PAID);
		invRepo.save(invoice);
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
	}

	public byte[] getAttach(String invoiceCode, String attachName) throws IOException {
		attachName = invoiceCode + "-" + attachName;
		Path path = Paths.get(server.getMerchantLocation() + "attachment/" + attachName);
		return Files.readAllBytes(path);
	}

	public Invoice createChild(String invoiceCode, Consumer consumer) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (CommonUtil.isNull(invoice) || !InvoiceType.BULK.equals(invoice.getInvoiceType())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Invoice");
		}
		Invoice childInvoice = invHelp.prepareChildInvoice(invoiceCode);
		consumer.setActive(true);
		consumer.setCreated(new Date());
		consumer.setCreatedBy(secSer.findLoggedInUser().getEmail());
		consumer.setMerchant(invoice.getMerchant());
		childInvoice.setConsumer(consumer);
		invHelp.updateConsumer(childInvoice, consumer);
		return childInvoice;
	}

	public void recurr(String invoiceCode, RecurringInvoice recInv) {
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
			Invoice childInvoice = invHelp.prepareChildInvoice(invoice.getInvoiceCode());
			Thread th = new Thread(invSchSer.processInvoice(recInv, childInvoice, timeNow));
			th.start();
		}
	}

	public List<RecurringInvoice> allRecurr(String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		return recInvRepo.findByInvoice(invoice);
	}

}
