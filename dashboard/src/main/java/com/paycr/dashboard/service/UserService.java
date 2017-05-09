package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.paycr.common.bean.Company;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.ResetPassword;
import com.paycr.common.data.repository.ResetPasswordRepository;
import com.paycr.common.type.ResetStatus;
import com.paycr.common.util.HmacSignerUtil;

import freemarker.template.Configuration;

@Service
public class UserService {

	@Autowired
	private Company company;

	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private ResetPasswordRepository resetRepo;

	@Autowired
	private Configuration fmConfiguration;

	@Autowired
	private HmacSignerUtil hmacSigner;

	public void sendResetLink(PcUser user) {
		Date timeNow = new Date();
		List<String> to = new ArrayList<String>();
		to.add(user.getEmail());
		List<String> cc = new ArrayList<String>();
		cc.add(user.getEmail());
		Email email = new Email(company.getContact(), to, cc);
		email.setSubject("Reset Password");
		String resetCode = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(timeNow.getTime()));
		ResetPassword resetPassword = new ResetPassword();
		resetPassword.setCreated(timeNow);
		resetPassword.setEmail(user.getEmail());
		resetPassword.setResetCode(resetCode);
		resetPassword.setStatus(ResetStatus.MAIL_SENT);
		resetRepo.save(resetPassword);
		String resetUrl = company.getBaseUrl() + "/resetPassword/" + resetCode;
		email.setMessage(
				"Hi, " + user.getName() + " please click on this link : " + resetUrl + " to reset your password");
		try {
			email.setMessage(getEmail(user, resetUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
		emailEngine.sendViaGmail(email);
	}

	public String getEmail(PcUser user, String resetUrl) throws Exception {
		Map<String, Object> templateProps = new HashMap<String, Object>();
		templateProps.put("name", user.getName());
		templateProps.put("resetUrl", resetUrl);
		templateProps.put("companyName", company.getName());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/reset_email.ftl"),
				templateProps);
	}

}
