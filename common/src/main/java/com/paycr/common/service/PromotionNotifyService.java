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
import com.paycr.common.data.domain.Promotion;
import com.paycr.common.data.repository.PromotionRepository;

import freemarker.template.Configuration;

@Service
public class PromotionNotifyService implements NotifyService<Promotion> {

	private static final Logger logger = LoggerFactory.getLogger(PromotionNotifyService.class);

	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private Company company;

	@Autowired
	private Configuration fmConfiguration;

	@Autowired
	private PromotionRepository promoRepo;

	@Async
	@Transactional
	public void notify(Promotion promotion) {
		List<String> to = new ArrayList<>();
		to.add(promotion.getEmail());
		List<String> cc = new ArrayList<>();
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		email.setSubject("Introducing PayCr!");
		try {
			email.setMessage(getEmail(promotion));
		} catch (Exception ex) {
			logger.error("Execption while generating email : {}", ex);
		}
		emailEngine.sendViaGmail(email);
		promotion.setSent(true);
		promotion.setNotified(promotion.getNotified() + 1);
		promoRepo.save(promotion);
	}

	public String getEmail(Promotion promotion) throws Exception {
		Map<String, Object> templateProps = new HashMap<>();
		templateProps.put("promotion", promotion);
		templateProps.put("baseUrl", company.getAppUrl());
		templateProps.put("staticUrl", company.getStaticUrl());
		return FreeMarkerTemplateUtils
				.processTemplateIntoString(fmConfiguration.getTemplate("email/promotion_email.ftl"), templateProps);
	}

	public String getSms(Promotion promotion) throws Exception {
		return null;
	}

}
