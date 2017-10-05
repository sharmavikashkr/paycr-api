package com.paycr.invoice.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCustomParam;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.domain.Timeline;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.TimelineRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ObjectType;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.invoice.helper.InvoiceHelper;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;

@Service
public class PaymentService {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private PaymentRepository payRepo;

	@Autowired
	private Company company;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Autowired
	private InvoiceHelper invHelp;

	@Autowired
	private TimelineRepository tlRepo;

	public ModelAndView payInvoice(String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		Timeline tl = new Timeline();
		tl.setCreatedBy("Consumer");
		tl.setCreated(new Date());
		tl.setInternal(false);
		tl.setMessage("Invoice payment initiated");
		tl.setObjectId(invoice.getId());
		tl.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tl);
		Merchant merchant = invoice.getMerchant();
		validate(invoice);
		if (InvoiceType.BULK.equals(invoice.getInvoiceType())) {
			invoice = invHelp.prepareChildInvoice(invoiceCode, "Consumer");
		}
		if (CommonUtil.isNull(invoice.getConsumer())) {
			ModelAndView mv = new ModelAndView("html/getconsumer");
			mv.addObject("staticUrl", company.getStaticUrl());
			mv.addObject("invoice", invoice);
			mv.addObject("signature", hmacSigner.signWithSecretKey(invoice.getInvoiceCode(), invoice.getInvoiceCode()));
			return mv;
		}
		ModelAndView mv = new ModelAndView("html/payinvoice");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("invoice", invoice);
		mv.addObject("banner", company.getAppUrl() + "/banner/merchant/" + merchant.getInvoiceSetting().getBanner());
		mv.addObject("rzpKeyId", merchant.getPaymentSetting().getRzpKeyId());
		mv.addObject("payAmount", String.valueOf(invoice.getPayAmount().multiply(new BigDecimal(100))));
		return mv;
	}

	private void validate(Invoice invoice) {
		if (CommonUtil.isNull(invoice)) {
			throw new PaycrException(Constants.FAILURE, "Requested Resource is not found");
		}
		if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "This invoice is already paid");
		}
		if (InvoiceStatus.EXPIRED.equals(invoice.getStatus()) && !InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "This invoice has expired");
		}
		Date timeNow = new Date();
		if (invoice.getExpiry() != null && invoice.getExpiry().before(timeNow)) {
			invoice.setStatus(InvoiceStatus.EXPIRED);
			invRepo.save(invoice);
			throw new PaycrException(Constants.FAILURE, "This invoice has expired");
		}
	}

	public String purchase(Map<String, String> formData) throws RazorpayException {
		String rzpPayId = formData.get("razorpay_payment_id");
		String invoiceCode = formData.get("invoiceCode");
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		Merchant merchant = invoice.getMerchant();
		for (InvoiceCustomParam param : invoice.getCustomParams()) {
			if (ParamValueProvider.CONSUMER.equals(param.getProvider())) {
				String paramValue = formData.get(param.getParamName());
				param.setParamValue(paramValue);
			}
		}
		PaymentSetting paymentSetting = merchant.getPaymentSetting();
		Payment payment = new Payment();
		payment.setCreated(new Date());
		payment.setInvoiceCode(invoice.getInvoiceCode());
		payment.setMerchant(merchant);
		payment.setPaymentRefNo(rzpPayId);
		capturePayment(invoice, payment, paymentSetting);
		return invoiceCode;
	}

	public void decline(String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		invoice.setStatus(InvoiceStatus.DECLINED);
		invRepo.save(invoice);
	}

	public void enquire(Invoice invoice) throws RazorpayException {
		Payment payment = invoice.getPayment();
		PaymentSetting paymentSetting = invoice.getMerchant().getPaymentSetting();
		capturePayment(invoice, payment, paymentSetting);
	}

	private void capturePayment(Invoice invoice, Payment payment, PaymentSetting paymentSetting)
			throws RazorpayException {
		Date timeNow = new Date();
		RazorpayClient razorpay = new RazorpayClient(paymentSetting.getRzpKeyId(), paymentSetting.getRzpSecretId());
		com.razorpay.Payment rzpPayment = razorpay.Payments.fetch(payment.getPaymentRefNo());
		JSONObject request = new JSONObject();
		request.put("amount", rzpPayment.get("amount").toString());
		if ("authorized".equals(rzpPayment.get("status"))) {
			rzpPayment = razorpay.Payments.capture(payment.getPaymentRefNo(), request);
		}
		payment.setStatus(rzpPayment.get("status"));
		invoice.setStatus(getStatus(rzpPayment.get("status")));
		payment.setMethod(rzpPayment.get("method"));
		payment.setAmount(invoice.getPayAmount());
		payment.setPayMode(PayMode.PAYCR);
		payment.setPayType(PayType.SALE);
		payment.setBank(JSONObject.NULL.equals(rzpPayment.get("bank")) ? null : rzpPayment.get("bank"));
		payment.setWallet(JSONObject.NULL.equals(rzpPayment.get("wallet")) ? null : rzpPayment.get("wallet"));
		invoice.setPayment(payment);
		invRepo.save(invoice);
		if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
			Notification noti = new Notification();
			noti.setMerchantId(invoice.getMerchant().getId());
			noti.setMessage("Payment received for Invoice# " + invoice.getInvoiceCode());
			noti.setSubject("Invoice Paid");
			noti.setCreated(timeNow);
			noti.setRead(false);
			notiRepo.save(noti);
		}
		Timeline tl = new Timeline();
		tl.setCreatedBy(invoice.getConsumer().getEmail());
		tl.setCreated(timeNow);
		tl.setInternal(false);
		tl.setMessage("Payment : " + payment.getPaymentRefNo() + " captured with status : " + payment.getStatus());
		tl.setObjectId(invoice.getId());
		tl.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tl);
	}

	public void refund(Invoice invoice, BigDecimal amount, String createdBy) throws RazorpayException {
		Date timeNow = new Date();
		Payment payment = invoice.getPayment();
		Merchant merchant = invoice.getMerchant();
		if (!PayMode.PAYCR.equals(payment.getPayMode())) {
			Payment refPay = new Payment();
			refPay.setAmount(amount);
			refPay.setCreated(new Date());
			refPay.setInvoiceCode(invoice.getInvoiceCode());
			refPay.setMerchant(merchant);
			refPay.setPaymentRefNo(payment.getPaymentRefNo());
			refPay.setStatus("refund");
			refPay.setPayMode(payment.getPayMode());
			refPay.setMethod(payment.getMethod());
			refPay.setPayType(PayType.REFUND);
			payRepo.save(refPay);
			return;
		}
		PaymentSetting paymentSetting = merchant.getPaymentSetting();
		RazorpayClient razorpay = new RazorpayClient(paymentSetting.getRzpKeyId(), paymentSetting.getRzpSecretId());
		String refundAmount = String.valueOf(amount.multiply(new BigDecimal(100)));
		JSONObject refundRequest = new JSONObject();
		refundRequest.put("amount", refundAmount);
		Refund refund = razorpay.Payments.refund(payment.getPaymentRefNo(), refundRequest);
		Payment refPay = new Payment();
		refPay.setAmount(amount);
		refPay.setCreated(timeNow);
		refPay.setInvoiceCode(invoice.getInvoiceCode());
		refPay.setMerchant(merchant);
		refPay.setPaymentRefNo(refund.get("id"));
		refPay.setStatus(refund.get("entity"));
		refPay.setPayMode(PayMode.PAYCR);
		refPay.setMethod(payment.getMethod());
		refPay.setPayType(PayType.REFUND);
		payRepo.save(refPay);
		Timeline tl = new Timeline();
		tl.setCreatedBy(createdBy);
		tl.setCreated(timeNow);
		tl.setInternal(true);
		tl.setMessage("Invoice refunded with amount : " + amount);
		tl.setObjectId(invoice.getId());
		tl.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tl);
	}

	private InvoiceStatus getStatus(String rzpStatus) {
		if ("captured".equals(rzpStatus)) {
			return InvoiceStatus.PAID;
		} else {
			return InvoiceStatus.UNPAID;
		}
	}

	public void updateConsumerAndPay(String invoiceCode, String name, String email, String mobile, String signature) {
		String genSig = hmacSigner.signWithSecretKey(invoiceCode, invoiceCode);
		if (!genSig.equals(signature)) {
			throw new PaycrException(Constants.FAILURE, "Invalid Signature");
		}
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (CommonUtil.isNull(invoice)) {
			throw new PaycrException(Constants.FAILURE, "Invalid Invoice");
		}
		if (!InvoiceType.SINGLE.equals(invoice.getInvoiceType())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Invoice type");
		}
		Consumer consumer = new Consumer();
		consumer.setEmail(email);
		consumer.setMobile(mobile);
		consumer.setName(name);
		consumer.setCreatedBy("SELF");
		invHelp.updateConsumer(invoice, consumer);
		Timeline tl = new Timeline();
		tl.setCreatedBy(email);
		tl.setCreated(new Date());
		tl.setInternal(false);
		tl.setMessage("Consumer added to invoice");
		tl.setObjectId(invoice.getId());
		tl.setObjectType(ObjectType.INVOICE);
		tlRepo.save(tl);
	}

}
