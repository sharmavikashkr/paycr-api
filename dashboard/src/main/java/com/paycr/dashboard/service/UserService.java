package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.paycr.common.bean.Company;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.ResetPassword;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.ResetPasswordRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.ResetStatus;
import com.paycr.common.type.Role;
import com.paycr.common.util.CommonUtil;
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

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private NotificationRepository notiRepo;

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

	public void createSuperAdmin() {
		Date timeNow = new Date();
		if (CommonUtil.isNotNull(userRepo.findByEmail("admin@paycr.in"))) {
			return;
		}
		PcUser user = new PcUser();
		user.setCreated(timeNow);
		user.setName("Paycr Admin");
		user.setEmail("admin@paycr.in");
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile("9977553311");
		List<UserRole> userRoles = new ArrayList<UserRole>();
		UserRole userRole = new UserRole();
		userRole.setRole(Role.ROLE_ADMIN);
		userRole.setPcUser(user);
		userRoles.add(userRole);
		user.setUserRoles(userRoles);
		user.setActive(true);
		userRepo.save(user);

		Notification noti = new Notification();
		noti.setUserId(user.getId());
		noti.setMessage("Hope you manage the product well :)");
		noti.setSubject("Welcome to Paycr");
		noti.setCreated(timeNow);
		noti.setRead(false);
		notiRepo.save(noti);
	}

	public PcUser getUserByEmail(String userEmail) {
		return userRepo.findByEmail(userEmail);
	}

	public PcUser saveUser(PcUser user) {
		return userRepo.save(user);
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

}
