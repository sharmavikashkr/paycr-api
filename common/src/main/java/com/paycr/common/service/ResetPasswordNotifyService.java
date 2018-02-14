package com.paycr.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.paycr.common.bean.Company;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.ResetPassword;
import com.paycr.common.data.repository.ResetPasswordRepository;
import com.paycr.common.type.ResetStatus;
import com.paycr.common.util.HmacSignerUtil;

import freemarker.template.Configuration;

@Service
public class ResetPasswordNotifyService implements NotifyService<PcUser> {

	private static final Logger logger = LoggerFactory.getLogger(ResetPasswordNotifyService.class);

	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private Company company;

	@Autowired
	private Configuration fmConfiguration;

	@Autowired
	private ResetPasswordRepository resetRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	public void notify(PcUser user) {
		logger.info("Sending reset password link to : {}", user.getEmail());
		List<String> to = new ArrayList<String>();
		to.add(user.getEmail());
		List<String> cc = new ArrayList<String>();
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		email.setSubject("Reset Password");
		try {
			email.setMessage(getEmail(user));
		} catch (Exception ex) {
			logger.error("Execption while generating email : {}", ex);
		}
		emailEngine.sendViaGmail(email);
	}

	public String getEmail(PcUser user) throws Exception {
		Date timeNow = new Date();
		String resetCode = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(timeNow.getTime()));
		ResetPassword resetPassword = new ResetPassword();
		resetPassword.setCreated(timeNow);
		resetPassword.setEmail(user.getEmail());
		resetPassword.setResetCode(resetCode);
		resetPassword.setStatus(ResetStatus.MAIL_SENT);
		resetRepo.save(resetPassword);
		String resetUrl = company.getAppUrl() + "/resetPassword/" + resetCode;
		Map<String, Object> templateProps = new HashMap<String, Object>();
		templateProps.put("name", user.getName());
		templateProps.put("resetUrl", resetUrl);
		templateProps.put("companyName", company.getName());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/reset_email.ftl"),
				templateProps);
	}

	public String getSms(PcUser user) throws Exception {
		return null;
	}

}
