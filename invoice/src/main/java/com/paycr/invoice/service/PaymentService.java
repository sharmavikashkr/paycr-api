package com.paycr.invoice.service;

import java.math.BigDecimal;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;

@Service
public class PaymentService {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private PaymentRepository payRepo;

	public void enquire(Invoice invoice) {
		Payment payment = invoice.getPayment();
		Merchant merchant = merRepo.findOne(invoice.getMerchant());
		MerchantSetting setting = merchant.getSetting();
		RazorpayClient razorpay = new RazorpayClient(setting.getRzpKeyId(), setting.getRzpSecretId());
		try {
			com.razorpay.Payment rzpPayment = razorpay.Payments.fetch(payment.getPaymentRefNo());
			if ("authorized".equals(rzpPayment.get("status"))) {
				JSONObject request = new JSONObject();
				request.put("amount", rzpPayment.get("amount").toString());
				rzpPayment = razorpay.Payments.capture(payment.getPaymentRefNo(), request);
			}
			payment.setStatus(rzpPayment.get("status"));
			invoice.setStatus(getStatus(rzpPayment.get("status")));
			payment.setMethod(rzpPayment.get("method"));
			payment.setBank(JSONObject.NULL.equals(rzpPayment.get("bank")) ? null : rzpPayment.get("bank"));
			payment.setWallet(JSONObject.NULL.equals(rzpPayment.get("wallet")) ? null : rzpPayment.get("wallet"));
			payment.setPayMode(PayMode.ONLINE);
			payment.setPayType(PayType.SALE);
			invoice.setPayment(payment);
			invRepo.save(invoice);
			if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
				Notification noti = new Notification();
				noti.setMerchantId(merchant.getId());
				noti.setMessage("Payment received for Invoice# " + invoice.getInvoiceCode());
				noti.setSubject("Invoice Paid");
				noti.setCreated(new Date());
				noti.setRead(false);
				notiRepo.save(noti);
			}
		} catch (Exception e) {
		}
	}

	public void refund(Invoice invoice, BigDecimal amount) {
		Payment payment = invoice.getPayment();
		if (PayMode.OFFLINE.equals(payment.getPayMode())) {
			Payment refPay = new Payment();
			refPay.setAmount(amount);
			refPay.setCreated(new Date());
			refPay.setInvoiceCode(invoice.getInvoiceCode());
			refPay.setPaymentRefNo("");
			refPay.setStatus("refund");
			refPay.setPayMode(PayMode.OFFLINE);
			refPay.setMethod(payment.getMethod());
			refPay.setPayType(PayType.REFUND);
			payRepo.save(refPay);
			return;
		}
		Merchant merchant = merRepo.findOne(invoice.getMerchant());
		MerchantSetting setting = merchant.getSetting();
		RazorpayClient razorpay = new RazorpayClient(setting.getRzpKeyId(), setting.getRzpSecretId());
		try {
			String refundAmount = String.valueOf(amount.multiply(new BigDecimal(100)));
			JSONObject refundRequest = new JSONObject();
			refundRequest.put("amount", refundAmount);
			Refund refund = razorpay.Payments.refund(payment.getPaymentRefNo(), refundRequest);
			Payment refPay = new Payment();
			refPay.setAmount(amount);
			refPay.setCreated(new Date());
			refPay.setInvoiceCode(invoice.getInvoiceCode());
			refPay.setPaymentRefNo(refund.get("id"));
			refPay.setStatus(refund.get("entity"));
			refPay.setPayMode(PayMode.ONLINE);
			refPay.setMethod(payment.getMethod());
			refPay.setPayType(PayType.REFUND);
			payRepo.save(refPay);
		} catch (Exception e) {
		}
	}

	private InvoiceStatus getStatus(String rzpStatus) {
		if ("captured".equals(rzpStatus)) {
			return InvoiceStatus.PAID;
		} else {
			return InvoiceStatus.UNPAID;
		}
	}

}
