package com.paycr.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.paycr.common.bean.Company;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.type.PayType;

import freemarker.template.Configuration;

@Service
public class PaymentNotifyService implements NotifyService<InvoicePayment> {

	private static final Logger logger = LoggerFactory.getLogger(PaymentNotifyService.class);

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private Company company;

	@Autowired
	private Configuration fmConfiguration;

	@Async
	@Transactional
	public void notify(InvoicePayment payment) {
		Invoice invoice = invRepo.findByInvoiceCode(payment.getInvoiceCode());
		Consumer consumer = invoice.getConsumer();
		if ((!consumer.isEmailOnPay() && PayType.SALE.equals(payment.getPayType()))
				|| (!consumer.isEmailOnRefund() && PayType.REFUND.equals(payment.getPayType()))) {
			return;
		}
		List<String> to = new ArrayList<>();
		to.add(invoice.getConsumer().getEmail());
		List<String> cc = new ArrayList<>();
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		if (PayType.SALE.equals(payment.getPayType())) {
			email.setSubject("Payment successful for invoice");
			email.setMessage(
					"Hi " + invoice.getConsumer().getName() + ", we have received successful payment towards invoice : "
							+ invoice.getInvoiceCode() + " for INR " + invoice.getPayAmount());
		} else {
			email.setSubject("Refund processed for invoice");
			email.setMessage(
					"Hi " + invoice.getConsumer().getName() + ", successful refund processed towards invoice : "
							+ invoice.getInvoiceCode() + " for INR " + invoice.getPayAmount());
		}
		try {
			email.setMessage(getEmail(payment));
		} catch (Exception ex) {
			logger.error("Execption while generating email : {}", ex);
		}
		emailEngine.sendViaGmail(email);
	}

	public String getEmail(InvoicePayment payment) throws Exception {
		Map<String, Object> templateProps = new HashMap<>();
		templateProps.put("payment", payment);
		templateProps.put("baseUrl", company.getAppUrl());
		templateProps.put("staticUrl", company.getStaticUrl());
		if (PayType.SALE.equals(payment.getPayType())) {
			templateProps.put("message", "Successful payment received for your invoice from");
		} else {
			templateProps.put("message", "Refund processed for your invoice from");
		}
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/payment_email.ftl"),
				templateProps);
	}

	public String getSms(InvoicePayment payment) throws Exception {
		return null;
	}

}
