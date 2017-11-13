package com.paycr.common.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.communicate.Sms;
import com.paycr.common.communicate.SmsEngine;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.util.PdfUtil;

import freemarker.template.Configuration;

@Service
public class PaymentNotifyService implements NotifyService<Payment> {

	@Autowired
	private SmsEngine smsEngine;

	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private Company company;

	@Autowired
	private Configuration fmConfiguration;

	@Autowired
	private Server server;

	@Autowired
	private PdfUtil pdfUtil;

	public void notify(Payment payment) {}

	public String getEmail(Payment payment) throws Exception {
		Map<String, Object> templateProps = new HashMap<>();
		templateProps.put("payment", payment);
		templateProps.put("invoiceUrl", company.getAppUrl() + "/" + payment.getInvoiceCode());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/invoice_email.ftl"),
				templateProps);
	}

	public String getSms(Payment payment) throws Exception {
		Map<String, Object> templateProps = new HashMap<>();
		templateProps.put("payment", payment);
		templateProps.put("invoiceUrl", company.getAppUrl() + "/" + payment.getInvoiceCode());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("sms/invoice_sms.ftl"),
				templateProps);
	}

}
