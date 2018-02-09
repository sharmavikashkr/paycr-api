package com.paycr.dashboard.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.ResetPassword;
import com.paycr.common.data.repository.ResetPasswordRepository;

@Service
public class AccessService {

	private static final Logger logger = LoggerFactory.getLogger(AccessService.class);

	@Autowired
	private ResetPasswordRepository resetRepo;

	@Autowired
	private NotifyService<PcUser> notifySer;

	public void sendResetLink(PcUser user) {
		logger.info("Sending reset password link for email : {}", user.getEmail());
		notifySer.notify(user);
	}

	public ResetPassword getResetPassword(String resetCode) {
		logger.info("Getting reset password details for code : {}", resetCode);
		ResetPassword resetPassword = resetRepo.findByResetCode(resetCode);
		return resetPassword;
	}

	public void saveResetPassword(ResetPassword resetPassword) {
		logger.info("Creating new ResetPassword : {}", new Gson().toJson(resetPassword));
		resetRepo.save(resetPassword);
	}

	public int findResetCount(String email, Date yesterday, Date timeNow) {
		return resetRepo.findResetCount(email, yesterday, timeNow);
	}

}
