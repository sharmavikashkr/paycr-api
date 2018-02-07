package com.paycr.dashboard.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.ResetPassword;
import com.paycr.common.data.repository.ResetPasswordRepository;

@Service
public class AccessService {

	@Autowired
	private ResetPasswordRepository resetRepo;

	@Autowired
	private NotifyService<PcUser> notifySer;

	public void sendResetLink(PcUser user) {
		notifySer.notify(user);
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
