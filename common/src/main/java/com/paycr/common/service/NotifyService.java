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
import com.paycr.common.communicate.Sms;
import com.paycr.common.communicate.SmsEngine;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.util.PdfUtil;

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

	@Autowired
	private Server server;

	@Autowired
	private PdfUtil pdfUtil;

	public void notify(Invoice invoice, InvoiceNotify invoiceNotify) {
		String invoiceUrl = company.getAppUrl() + "/" + invoice.getInvoiceCode();
		Merchant merchant = invoice.getMerchant();
		if (invoiceNotify.isSendSms()) {
			Sms sms = new Sms();
			sms.setTo(invoice.getConsumer().getMobile());
			try {
				sms.setMessage(getSms(invoice));
				smsEngine.send(sms);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (invoiceNotify.isSendEmail()) {
			List<String> to = new ArrayList<>();
			to.add(invoice.getConsumer().getEmail());
			List<String> cc = new ArrayList<>();
			if (invoiceNotify.isCcMe()) {
				cc.add(invoiceNotify.getCcEmail());
			}
			Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(),
					to, cc);
			email.setSubject(invoiceNotify.getEmailSubject());
			email.setMessage("Hi, " + invoice.getConsumer().getName() + " please click on this link : " + invoiceUrl
					+ " to pay INR " + invoice.getPayAmount() + " towards " + merchant.getName());
			try {
				email.setMessage(getEmail(invoice, invoiceNotify.getEmailNote()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (invoiceNotify.isEmailPdf()) {
				try {
					String fileName = invoice.getInvoiceCode() + ".pdf";
					String pdfPath = server.getInvoiceLocation() + fileName;
					File pdfFile = new File(pdfPath);
					if (!pdfFile.exists()) {
						pdfFile.createNewFile();
						pdfUtil.makePdf(company.getAppUrl() + "/invoice/receipt/" + invoice.getInvoiceCode(),
								pdfFile.getAbsolutePath());
					}
					email.setFileName(fileName);
					email.setFilePath(pdfPath);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			emailEngine.sendViaGmail(email);
		}
	}

	public String getEmail(Invoice invoice, String note) throws Exception {
		Map<String, Object> templateProps = new HashMap<>();
		templateProps.put("invoice", invoice);
		templateProps.put("note", note);
		templateProps.put("invoiceUrl", company.getAppUrl() + "/" + invoice.getInvoiceCode());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/invoice_email.ftl"),
				templateProps);
	}

	public String getSms(Invoice invoice) throws Exception {
		Map<String, Object> templateProps = new HashMap<>();
		templateProps.put("invoice", invoice);
		templateProps.put("invoiceUrl", company.getAppUrl() + "/" + invoice.getInvoiceCode());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("sms/invoice_sms.ftl"),
				templateProps);
	}

}
