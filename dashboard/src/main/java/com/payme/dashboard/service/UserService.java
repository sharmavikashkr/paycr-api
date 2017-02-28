package com.payme.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.payme.common.bean.Company;
import com.payme.common.communicate.Email;
import com.payme.common.communicate.EmailEngine;
import com.payme.common.data.domain.PmUser;
import com.payme.common.data.domain.ResetPassword;
import com.payme.common.data.repository.ResetPasswordRepository;
import com.payme.common.type.ResetStatus;
import com.payme.common.util.HmacSignerUtil;

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

	public void sendResetLink(PmUser user) {
		Date timeNow = new Date();
		List<String> to = new ArrayList<String>();
		to.add(user.getEmail());
		List<String> cc = new ArrayList<String>();
		cc.add(user.getEmail());
		Email email = new Email("sharma.vikashkr@gmail.com", to, cc);
		email.setSubject("Reset Password");
		String resetCode = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(timeNow.getTime()));
		ResetPassword resetPassword = new ResetPassword();
		resetPassword.setCreated(timeNow);
		resetPassword.setEmail(user.getEmail());
		resetPassword.setResetCode(resetCode);
		resetPassword.setStatus(ResetStatus.MAIL_SENT);
		resetRepo.save(resetPassword);
		String resetUrl = company.getBaseUrl() + "/user/resetPassword/" + resetCode;
		email.setMessage(
				"Hi, " + user.getName() + " please click on this link : " + resetUrl + " to reset your password");
		try {
			email.setMessage(getEmail(user, resetUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
		emailEngine.send(email);
	}

	public String getEmail(PmUser user, String resetUrl) throws Exception {
		Map<String, Object> templateProps = new HashMap<String, Object>();
		templateProps.put("name", user.getName());
		templateProps.put("resetUrl", resetUrl);
		templateProps.put("companyName", company.getName());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/reset_email.ftl"),
				templateProps);
	}

}
