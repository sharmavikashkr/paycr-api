package com.paycr.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.paycr.common.bean.Company;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.communicate.Sms;
import com.paycr.common.communicate.SmsEngine;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;

import freemarker.template.Configuration;

@Service
public class NotifyService {

	@Autowired
	private SmsEngine smsEngine;

	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private Company company;

	@Autowired
	private Configuration fmConfiguration;

	public void notify(Invoice invoice) {
		String invoiceUrl = company.getBaseUrl() + "/" + invoice.getInvoiceCode();
		Merchant merchant = invoice.getMerchant();
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
			Email email = new Email(company.getContact(), to, cc);
			email.setSubject("Invoice for your order");
			email.setMessage("Hi, " + invoice.getConsumer().getName() + " please click on this link : " + invoiceUrl
					+ " to pay INR " + invoice.getPayAmount() + " towards " + merchant.getName());
			try {
				email.setMessage(getEmail(invoice));
			} catch (Exception e) {
				e.printStackTrace();
			}
			emailEngine.sendViaGmail(email);
		}
	}

	public String getEmail(Invoice invoice) throws Exception {
		Merchant merchant = invoice.getMerchant();
		Map<String, Object> templateProps = new HashMap<String, Object>();
		templateProps.put("invoice", invoice);
		templateProps.put("merchant", merchant);
		templateProps.put("invoiceUrl", company.getBaseUrl() + "/" + invoice.getInvoiceCode());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/invoice_email.ftl"),
				templateProps);
	}

}
