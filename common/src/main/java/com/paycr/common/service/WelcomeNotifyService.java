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
import com.paycr.common.data.domain.Merchant;

import freemarker.template.Configuration;

@Service
public class WelcomeNotifyService implements NotifyService<Merchant> {

	private static final Logger logger = LoggerFactory.getLogger(WelcomeNotifyService.class);

	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private Company company;

	@Autowired
	private Configuration fmConfiguration;

	@Async
	@Transactional
	public void notify(Merchant merchant) {
		List<String> to = new ArrayList<>();
		to.add(merchant.getEmail());
		List<String> cc = new ArrayList<>();
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		email.setSubject("Welcome to PayCr!");
		try {
			email.setMessage(getEmail(merchant));
		} catch (Exception ex) {
			logger.error("Execption while generating email : {}", ex);
		}
		emailEngine.sendViaGmail(email);
	}

	public String getEmail(Merchant merchant) throws Exception {
		Map<String, Object> templateProps = new HashMap<>();
		templateProps.put("merchant", merchant);
		templateProps.put("webUrl", company.getWebUrl());
		templateProps.put("staticUrl", company.getStaticUrl());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/welcome_email.ftl"),
				templateProps);
	}

	public String getSms(Merchant merchant) throws Exception {
		return null;
	}

}
