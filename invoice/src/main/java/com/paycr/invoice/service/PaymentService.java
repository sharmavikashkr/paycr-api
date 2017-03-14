package com.paycr.invoice.service;

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
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.util.CommonUtil;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentService {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private NotificationRepository notiRepo;

	public void enquire(Invoice invoice) {
		Payment payment = invoice.getPayment();
		Merchant merchant = merRepo.findOne(invoice.getMerchant());
		MerchantSetting setting = merchant.getSetting();
		RazorpayClient razorpay = new RazorpayClient(setting.getRzpKeyId(), setting.getRzpSecretId());
		if (CommonUtil.isNotNull(payment)) {
			try {
				com.razorpay.Payment rzpPayment = razorpay.Payments.fetch(payment.getPaymentRefNo());
				payment.setStatus(rzpPayment.get("status"));
				invoice.setStatus(getStatus(rzpPayment.get("status")));
				payment.setMethod(rzpPayment.get("method"));
				payment.setBank(JSONObject.NULL.equals(rzpPayment.get("bank")) ? null : rzpPayment.get("bank"));
				payment.setWallet(JSONObject.NULL.equals(rzpPayment.get("wallet")) ? null : rzpPayment.get("wallet"));
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
			} catch (RazorpayException e) {
				e.printStackTrace();
			}
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
