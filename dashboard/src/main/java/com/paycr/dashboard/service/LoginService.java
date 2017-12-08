package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.paycr.common.bean.Company;
import com.paycr.common.client.RestTemplateClient;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.ResetPassword;
import com.paycr.common.data.repository.ResetPasswordRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ResetStatus;
import com.paycr.common.util.Constants;
import com.paycr.common.util.HmacSignerUtil;

import freemarker.template.Configuration;

@Service
public class LoginService {

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
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		email.setSubject("Reset Password");
		String resetCode = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(timeNow.getTime()));
		ResetPassword resetPassword = new ResetPassword();
		resetPassword.setCreated(timeNow);
		resetPassword.setEmail(user.getEmail());
		resetPassword.setResetCode(resetCode);
		resetPassword.setStatus(ResetStatus.MAIL_SENT);
		resetRepo.save(resetPassword);
		String resetUrl = company.getAppUrl() + "/resetPassword/" + resetCode;
		email.setMessage(
				"Hi, " + user.getName() + " please click on this link : " + resetUrl + " to reset your password");
		try {
			email.setMessage(getResetPassEmailBody(user, resetUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
		emailEngine.sendViaGmail(email);
	}

	public String getResetPassEmailBody(PcUser user, String resetUrl) throws Exception {
		Map<String, Object> templateProps = new HashMap<String, Object>();
		templateProps.put("name", user.getName());
		templateProps.put("resetUrl", resetUrl);
		templateProps.put("companyName", company.getName());
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/reset_email.ftl"),
				templateProps);
	}

	public ResetPassword getResetPassword(String resetCode) {
		ResetPassword resetPassword = resetRepo.findByResetCode(resetCode);
		return resetPassword;
	}

	public void saveResetPassword(ResetPassword resetPassword) {
		resetRepo.save(resetPassword);
	}

	public int findResetCount(String email, Date yesterday, Date timeNow) {
		return resetRepo.findResetCount(email, yesterday, timeNow);
	}

	public LinkedHashMap secureLogin(String email, String password) {
		try {
			RestTemplate restTemplate = RestTemplateClient.getRestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			header.add("Authorization", "Basic d2ViLWNsaWVudDozYjVlOGViM2ZjZmFmYTJlN2IzMDJmNzVjMGUxODVkMzNkODY5MGMy");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("username", email);
			map.add("password", password);
			map.add("grant_type", "password");
			HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, header);
			ResponseEntity<LinkedHashMap> response = restTemplate.exchange(company.getOauthUrl() + "/oauth/token",
					HttpMethod.POST, httpEntity, LinkedHashMap.class);
			return response.getBody();
		} catch (Exception ex) {
			throw new PaycrException(Constants.FAILURE, "Something went wrong");
		}
	}

}
