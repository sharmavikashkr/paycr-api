package com.payme.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payme.common.bean.Payme;
import com.payme.common.communicate.Sms;
import com.payme.common.communicate.SmsEngine;
import com.payme.common.data.domain.Invoice;
import com.payme.common.data.domain.Merchant;
import com.payme.common.data.repository.MerchantRepository;

@Service
public class NotifyService {

	@Autowired
	private SmsEngine smsEngine;

	@Autowired
	private Payme payme;

	@Autowired
	private MerchantRepository merRepo;

	public void notify(Invoice invoice) {
		String invoiceUrl = payme.getBaseUrl() + "/" + invoice.getInvoiceCode();
		Merchant merchant = merRepo.findOne(invoice.getMerchant());
		if (invoice.isSendSms()) {
			Sms sms = new Sms();
			sms.setTo(invoice.getConsumer().getMobile());
			sms.setMessage("Please click on this link : " + invoiceUrl + " to pay INR " + invoice.getPayAmount()
					+ " towards " + merchant.getName());
			smsEngine.send(sms);
		}
	}

}
