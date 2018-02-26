package com.paycr.invoice.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCustomParam;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.repository.InvoicePaymentRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ObjectType;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.invoice.helper.InvoiceHelper;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;

@Service
public class PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private InvoicePaymentRepository payRepo;

	@Autowired
	private NotifyService<InvoicePayment> payNotSer;

	@Autowired
	private Company company;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Autowired
	private InvoiceHelper invHelp;

	@Autowired
	private TimelineService tlService;

	public ModelAndView payInvoice(String invoiceCode) {
		logger.info("Invoice payment : {}", invoiceCode);
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Invoice payment initiated", true, "Consumer");
		Merchant merchant = invoice.getMerchant();
		validate(invoice);
		if (InvoiceType.BULK.equals(invoice.getInvoiceType()) || CommonUtil.isNull(invoice.getConsumer())) {
			ModelAndView mv = new ModelAndView("html/getconsumer");
			mv.addObject("staticUrl", company.getStaticUrl());
			mv.addObject("webUrl", company.getWebUrl());
			mv.addObject("banner", company.getAppUrl() + "/banner/merchant/" + merchant.getBanner());
			mv.addObject("invoice", invoice);
			mv.addObject("signature", hmacSigner.signWithSecretKey(merchant.getSecretKey(), invoice.getInvoiceCode()));
			return mv;
		}
		if (CommonUtil.isNull(merchant.getPaymentSetting())
				|| CommonUtil.isEmpty(merchant.getPaymentSetting().getRzpMerchantId())
				|| CommonUtil.isEmpty(merchant.getPaymentSetting().getRzpKeyId())
				|| CommonUtil.isEmpty(merchant.getPaymentSetting().getRzpSecretId())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Online Payment not available");
		}
		ModelAndView mv = new ModelAndView("html/payinvoice");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("webUrl", company.getWebUrl());
		mv.addObject("invoice", invoice);
		mv.addObject("signature", hmacSigner.signWithSecretKey(merchant.getSecretKey(), invoice.getInvoiceCode()));
		mv.addObject("banner",
				company.getAppUrl() + "/banner/merchant/" + merchant.getAccessKey() + "/" + merchant.getBanner());
		mv.addObject("rzpKeyId", merchant.getPaymentSetting().getRzpKeyId());
		mv.addObject("payAmount", String.valueOf(
				invoice.getPayAmount().setScale(2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))));
		return mv;
	}

	private void validate(Invoice invoice) {
		if (CommonUtil.isNull(invoice)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Requested Resource is not found");
		}
		if (InvoiceType.RECURRING.equals(invoice.getInvoiceType())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "This invoice cannot be paid");
		}
		if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "This invoice is already paid");
		}
		if (InvoiceStatus.EXPIRED.equals(invoice.getStatus()) && !InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "This invoice has expired");
		}
		Date timeNow = new Date();
		if (invoice.getExpiry().before(timeNow)) {
			invoice.setStatus(InvoiceStatus.EXPIRED);
			invRepo.save(invoice);
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "This invoice has expired");
		}
	}

	public String purchase(Map<String, String> formData) throws RazorpayException {
		Date timeNow = new Date();
		String rzpPayId = formData.get("razorpay_payment_id");
		String invoiceCode = formData.get("invoiceCode");
		String signature = formData.get("signature");
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		Merchant merchant = invoice.getMerchant();
		if (!hmacSigner.signWithSecretKey(merchant.getSecretKey(), invoice.getInvoiceCode()).equals(signature)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Signature mismatch");
		}
		if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invoice already paid");
		}
		for (InvoiceCustomParam param : invoice.getCustomParams()) {
			if (ParamValueProvider.CONSUMER.equals(param.getProvider())) {
				String paramValue = formData.get(param.getParamName());
				param.setParamValue(paramValue);
			}
		}
		PaymentSetting paymentSetting = merchant.getPaymentSetting();
		InvoicePayment payment = new InvoicePayment();
		payment.setCreated(timeNow);
		payment.setPaidDate(timeNow);
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
		InvoicePayment payment = invoice.getPayment();
		PaymentSetting paymentSetting = invoice.getMerchant().getPaymentSetting();
		capturePayment(invoice, payment, paymentSetting);
	}

	private void capturePayment(Invoice invoice, InvoicePayment payment, PaymentSetting paymentSetting)
			throws RazorpayException {
		RazorpayClient razorpay = new RazorpayClient(paymentSetting.getRzpKeyId(), paymentSetting.getRzpSecretId());
		com.razorpay.Payment rzpPayment = razorpay.Payments.fetch(payment.getPaymentRefNo());
		JSONObject request = new JSONObject();
		request.put("amount", rzpPayment.get("amount").toString());
		if ("authorized".equals(rzpPayment.get("status"))) {
			rzpPayment = razorpay.Payments.capture(payment.getPaymentRefNo(), request);
		}
		payment.setStatus(rzpPayment.get("status"));
		invoice.setStatus(getStatus(rzpPayment.get("status")));
		payment.setAmount(invoice.getPayAmount());
		payment.setPayMode(PayMode.PAYCR);
		payment.setPayType(PayType.SALE);
		StringBuilder method = new StringBuilder(rzpPayment.get("method"));
		method.append(JSONObject.NULL.equals(rzpPayment.get("bank")) ? "" : " - " + rzpPayment.get("bank"));
		method.append(JSONObject.NULL.equals(rzpPayment.get("wallet")) ? "" : " - " + rzpPayment.get("wallet"));
		payment.setMethod(method.toString());
		invoice.setPayment(payment);
		invRepo.save(invoice);
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE,
				"Payment : " + payment.getPaymentRefNo() + " captured with status : " + payment.getStatus(), true,
				invoice.getConsumer().getEmail());
		if ("captured".equals(payment.getStatus())) {
			payNotSer.notify(payment);
		}
	}

	public InvoicePayment refund(Invoice invoice, BigDecimal amount, String createdBy) throws RazorpayException {
		logger.info("Refund Invoice : {}", invoice.getInvoiceCode());
		Date timeNow = new Date();
		InvoicePayment payment = invoice.getPayment();
		Merchant merchant = invoice.getMerchant();
		InvoicePayment refPay = new InvoicePayment();
		if (!PayMode.PAYCR.equals(payment.getPayMode())) {
			refPay.setAmount(amount);
			refPay.setCreated(timeNow);
			refPay.setPaidDate(timeNow);
			refPay.setInvoiceCode(invoice.getInvoiceCode());
			refPay.setMerchant(merchant);
			refPay.setPaymentRefNo(payment.getPaymentRefNo());
			refPay.setStatus("refund");
			refPay.setPayMode(payment.getPayMode());
			refPay.setMethod(payment.getMethod());
			refPay.setPayType(PayType.REFUND);
			payRepo.save(refPay);
		} else {
			PaymentSetting paymentSetting = merchant.getPaymentSetting();
			RazorpayClient razorpay = new RazorpayClient(paymentSetting.getRzpKeyId(), paymentSetting.getRzpSecretId());
			Integer refundAmount = amount.setScale(2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
					.intValue();
			JSONObject refundRequest = new JSONObject();
			refundRequest.put("amount", refundAmount);
			Refund refund = razorpay.Payments.refund(payment.getPaymentRefNo(), refundRequest);
			refPay.setAmount(amount);
			refPay.setCreated(timeNow);
			refPay.setPaidDate(timeNow);
			refPay.setInvoiceCode(invoice.getInvoiceCode());
			refPay.setMerchant(merchant);
			refPay.setPaymentRefNo(refund.get("id"));
			refPay.setStatus(refund.get("entity"));
			refPay.setPayMode(PayMode.PAYCR);
			refPay.setMethod(payment.getMethod());
			refPay.setPayType(PayType.REFUND);
			payRepo.save(refPay);
		}
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Invoice refunded with amount : " + amount, true,
				createdBy);
		if ("captured".equals(payment.getStatus())) {
			payNotSer.notify(refPay);
		}
		return refPay;
	}

	private InvoiceStatus getStatus(String rzpStatus) {
		if ("captured".equals(rzpStatus)) {
			return InvoiceStatus.PAID;
		} else {
			return InvoiceStatus.UNPAID;
		}
	}

	public String updateConsumerAndPay(String invoiceCode, String name, String email, String mobile, String signature) {
		logger.info("Update consumer for bulk Invoice : {}", invoiceCode);
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (CommonUtil.isNull(invoice) || !InvoiceType.BULK.equals(invoice.getInvoiceType())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Invoice");
		}
		String genSig = hmacSigner.signWithSecretKey(invoice.getMerchant().getSecretKey(), invoiceCode);
		if (!genSig.equals(signature)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Signature");
		}
		Invoice childInvoice = invHelp.prepareChildInvoice(invoiceCode, InvoiceType.SINGLE, "Consumer");
		Consumer consumer = new Consumer();
		consumer.setEmail(email);
		consumer.setMobile(mobile);
		consumer.setName(name);
		consumer.setCreatedBy("SELF");
		invHelp.updateConsumer(childInvoice, consumer);
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Consumer added to invoice", true, email);
		return childInvoice.getInvoiceCode();
	}

}
