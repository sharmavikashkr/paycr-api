package com.paycr.dashboard.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.ResetPassword;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ResetStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.dashboard.service.AccessService;
import com.paycr.dashboard.service.UserService;

@RestController
public class AccessController {

	@Autowired
	private PasswordEncoder passEncoder;

	@Autowired
	private AccessService accessService;

	@Autowired
	private UserService userService;

	@Autowired
	private Company company;

	@RequestMapping("/healthCheck")
	public String healthCheck() {
		return "SUCCESS";
	}

	@RequestMapping(value = "/sendResetPassword", method = RequestMethod.POST)
	public void sendResetPassword(@RequestParam("email") String userEmail) {
		PcUser user = userService.getUserByEmail(userEmail);
		Date timeNow = new Date();
		if (CommonUtil.isNotNull(user)) {
			Date yesterday = DateUtil.addDays(timeNow, -1);
			if (accessService.findResetCount(userEmail, yesterday, timeNow) >= 3) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Reset already requested 3 times in 24 hours");
			} else {
				accessService.sendResetLink(user);
			}
		} else {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "User not registered");
		}
	}

	@RequestMapping("/resetPassword/{resetCode}")
	public ModelAndView resetPasswordGet(@PathVariable String resetCode, HttpServletResponse httpResponse)
			throws IOException {
		ResetPassword resetPassword = accessService.getResetPassword(resetCode);
		if (!validateResetRequest(resetPassword)) {
			httpResponse.sendRedirect(company.getWebUrl() + "/forgot-password");
		}
		resetPassword.setStatus(ResetStatus.INTITIATED);
		accessService.saveResetPassword(resetPassword);
		ModelAndView mv = new ModelAndView("html/reset-password");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("webUrl", company.getWebUrl());
		mv.addObject("email", resetPassword.getEmail());
		mv.addObject("resetCode", resetCode);
		return mv;
	}

	@RequestMapping(value = "/resetPassword/{resetCode}", method = RequestMethod.POST)
	public void resetPasswordPost(@PathVariable String resetCode, @RequestParam("password") String password,
			HttpServletResponse httpResponse) throws IOException {
		ResetPassword resetPassword = accessService.getResetPassword(resetCode);
		if (!validateResetRequest(resetPassword)) {
			httpResponse.sendRedirect(company.getWebUrl() + "/forgot-password");
		}
		PcUser user = userService.getUserByEmail(resetPassword.getEmail());
		user.setPassword(passEncoder.encode(password));
		userService.saveUser(user);
		resetPassword.setStatus(ResetStatus.SUCCESS);
		accessService.saveResetPassword(resetPassword);
		httpResponse.sendRedirect(company.getWebUrl() + "/reset-success");
	}

	private boolean validateResetRequest(ResetPassword resetPassword) {
		if (CommonUtil.isNull(resetPassword)) {
			return false;
		}
		Date dayAfterCreation = DateUtil.addDays(resetPassword.getCreated(), 1);
		if (dayAfterCreation.compareTo(new Date()) <= 0) {
			return false;
		}
		if (ResetStatus.SUCCESS.equals(resetPassword.getStatus())) {
			return false;
		}
		return true;
	}

}
