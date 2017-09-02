package com.paycr.invoice.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCustomParam;
import com.paycr.common.data.domain.Item;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.invoice.validation.IsValidInvoiceConsumer;
import com.paycr.invoice.validation.IsValidInvoiceMerchantPricing;
import com.paycr.invoice.validation.IsValidInvoiceRequest;
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
	private MerchantPricingRepository merPriRepo;

	@Autowired
	private IsValidInvoiceRequest isValidRequest;

	@Autowired
	private IsValidInvoiceConsumer isValidConsumer;

	@Autowired
	private IsValidInvoiceMerchantPricing isValidPricing;

	public ModelAndView payInvoice(String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		Merchant merchant = invoice.getMerchant();
		validate(invoice);
		if (InvoiceType.BULK.equals(invoice.getInvoiceType())) {
			invoice = prepareChildInvoice(invoice);
		}
		if (CommonUtil.isNull(invoice.getConsumer())) {
			ModelAndView mv = new ModelAndView("html/getconsumer");
			mv.addObject("invoice", invoice);
			mv.addObject("signature", hmacSigner.signWithSecretKey(invoice.getInvoiceCode(), invoice.getInvoiceCode()));
			return mv;
		}
		ModelAndView mv = new ModelAndView("html/payinvoice");
		mv.addObject("invoice", invoice);
		mv.addObject("banner", company.getBaseUrl() + "/banner/merchant/" + merchant.getInvoiceSetting().getBanner());
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
		if (invoice.getExpiry().before(timeNow)) {
			invoice.setStatus(InvoiceStatus.EXPIRED);
			invRepo.save(invoice);
			throw new PaycrException(Constants.FAILURE, "This invoice has expired");
		}
	}

	private Invoice prepareChildInvoice(Invoice invoice) {
		Invoice childInvoice = ObjectUtils.clone(invoice);
		childInvoice.setId(null);
		childInvoice.setInvoiceCode(null);
		childInvoice.setParent(invoice);
		childInvoice.setMerchant(invoice.getMerchant());
		List<Item> newItems = new ArrayList<Item>();
		for (Item item : invoice.getItems()) {
			Item newItem = new Item();
			newItem.setInventory(item.getInventory());
			newItem.setQuantity(item.getQuantity());
			newItem.setPrice(item.getPrice());
			newItem.setInvoice(childInvoice);
			newItems.add(newItem);
		}
		childInvoice.setItems(newItems);
		List<InvoiceCustomParam> params = new ArrayList<InvoiceCustomParam>();
		for (InvoiceCustomParam param : invoice.getCustomParams()) {
			InvoiceCustomParam newParam = new InvoiceCustomParam();
			newParam.setParamName(param.getParamName());
			newParam.setParamValue(param.getParamValue());
			newParam.setProvider(param.getProvider());
			param.setInvoice(childInvoice);
		}
		childInvoice.setAttachments(null);
		childInvoice.setInvoiceNotices(null);
		childInvoice.setCustomParams(params);
		isValidRequest.validate(childInvoice);
		isValidPricing.validate(childInvoice);
		childInvoice.setInvoiceType(InvoiceType.SINGLE);
		childInvoice = invRepo.save(childInvoice);
		MerchantPricing merPri = childInvoice.getMerchantPricing();
		merPri.setInvCount(merPri.getInvCount() + 1);
		merPriRepo.save(merPri);
		return childInvoice;
	}

	public String purchase(Map<String, String> formData) throws IOException, RazorpayException {
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

	public void decline(String invoiceCode) throws IOException {
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
			noti.setCreated(new Date());
			noti.setRead(false);
			notiRepo.save(noti);
		}
	}

	public void refund(Invoice invoice, BigDecimal amount) throws RazorpayException {
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
		refPay.setCreated(new Date());
		refPay.setInvoiceCode(invoice.getInvoiceCode());
		refPay.setMerchant(merchant);
		refPay.setPaymentRefNo(refund.get("id"));
		refPay.setStatus(refund.get("entity"));
		refPay.setPayMode(PayMode.PAYCR);
		refPay.setMethod(payment.getMethod());
		refPay.setPayType(PayType.REFUND);
		payRepo.save(refPay);
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
		Consumer consumer = new Consumer();
		consumer.setActive(true);
		consumer.setCreated(new Date());
		consumer.setCreatedBy("SELF");
		consumer.setEmail(email);
		consumer.setMerchant(invoice.getMerchant());
		consumer.setMobile(mobile);
		consumer.setName(name);
		invoice.setConsumer(consumer);
		isValidConsumer.validate(invoice);
		invRepo.save(invoice);
	}

}
