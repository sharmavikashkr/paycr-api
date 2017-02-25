package com.payme.common.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payme.common.bean.Payme;
import com.payme.common.communicate.Email;
import com.payme.common.communicate.EmailEngine;
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
	private EmailEngine emailEngine;

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
			sms.setMessage("Hi, " + invoice.getConsumer().getName() + " please click on this link : " + invoiceUrl
					+ " to pay INR " + invoice.getPayAmount() + " towards " + merchant.getName());
			smsEngine.send(sms);
		}

		if (invoice.isSendEmail()) {
			List<String> to = new ArrayList<String>();
			to.add(invoice.getConsumer().getEmail());
			List<String> cc = new ArrayList<String>();
			cc.add("sharma.vikashkr@gmail.com");
			Email email = new Email("sharma.vikashkr@gmail.com", to, cc);
			email.setSubject("Payment for your order");
			email.setMessage("Hi, " + invoice.getConsumer().getName() + " please click on this link : " + invoiceUrl
					+ " to pay INR " + invoice.getPayAmount() + " towards " + merchant.getName());
			emailEngine.send(email);
		}
	}

}
