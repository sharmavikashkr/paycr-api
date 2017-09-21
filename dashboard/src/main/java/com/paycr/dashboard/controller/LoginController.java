package com.paycr.dashboard.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.ResetPassword;
import com.paycr.common.type.ResetStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.dashboard.service.LoginService;
import com.paycr.dashboard.service.UserService;

@RestController
public class LoginController {

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private LoginService loginService;

	@Autowired
	private UserService userService;

	@Autowired
	private Company company;

	@RequestMapping("/")
	public ModelAndView index() {
		ModelAndView mv = new ModelAndView("html/index");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
	}

	@RequestMapping("/login")
	public ModelAndView login(HttpServletResponse response) {
		response.addCookie(new Cookie("access_token", ""));
		ModelAndView mv = new ModelAndView("html/login");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
	}

	@RequestMapping("/adminlogin")
	public ModelAndView adminlogin(HttpServletResponse response) {
		response.addCookie(new Cookie("access_token", ""));
		ModelAndView mv = new ModelAndView("html/adminlogin");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
	}

	@RequestMapping("/forgotPassword")
	public ModelAndView forgotPasssword(@RequestParam(value = "error", required = false) String code) {
		ModelAndView mv = new ModelAndView("html/forgot-password");
		String message = "Enter Email to send reset password link";
		boolean isError = false;
		if ("1".equals(code)) {
			message = "User not registered";
			isError = true;
		} else if ("2".equals(code)) {
			message = "Reset already requested 3 times in 24 hours";
			isError = true;
		}
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("message", message);
		mv.addObject("isError", isError);
		return mv;
	}

	@RequestMapping(value = "/sendResetPassword", method = RequestMethod.POST)
	public void sendResetPassword(@RequestParam("email") String userEmail, HttpServletResponse response)
			throws IOException {
		PcUser user = userService.getUserByEmail(userEmail);
		Date timeNow = new Date();
		if (CommonUtil.isNotNull(user)) {
			Date yesterday = DateUtil.addDays(timeNow, -1);
			if (loginService.findResetCount(userEmail, yesterday, timeNow) >= 3) {
				response.sendRedirect("/forgotPassword?error=2");
			} else {
				loginService.sendResetLink(user);
				response.sendRedirect("/login");
			}
		} else {
			response.sendRedirect("/forgotPassword?error=1");
		}
	}

	@RequestMapping("/resetPassword/{resetCode}")
	public ModelAndView resetPasswordGet(@PathVariable String resetCode) {
		ResetPassword resetPassword = loginService.getResetPassword(resetCode);
		ModelAndView mvError = validateResetRequest(resetPassword);
		if (CommonUtil.isNotNull(mvError)) {
			return mvError;
		}
		resetPassword.setStatus(ResetStatus.INTITIATED);
		loginService.saveResetPassword(resetPassword);
		ModelAndView mv = new ModelAndView("html/reset-password");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("email", resetPassword.getEmail());
		mv.addObject("resetCode", resetCode);
		return mv;
	}

	@RequestMapping(value = "/resetPassword/{resetCode}", method = RequestMethod.POST)
	public ModelAndView resetPasswordPost(@PathVariable String resetCode, @RequestParam("password") String password) {
		ResetPassword resetPassword = loginService.getResetPassword(resetCode);
		ModelAndView mvError = validateResetRequest(resetPassword);
		if (CommonUtil.isNotNull(mvError)) {
			return mvError;
		}
		PcUser user = userService.getUserByEmail(resetPassword.getEmail());
		user.setPassword(bcPassEncode.encode(password));
		userService.saveUser(user);
		resetPassword.setStatus(ResetStatus.SUCCESS);
		loginService.saveResetPassword(resetPassword);
		ModelAndView mv = new ModelAndView("html/login");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
	}

	private ModelAndView validateResetRequest(ResetPassword resetPassword) {
		if (CommonUtil.isNull(resetPassword)) {
			ModelAndView mv = new ModelAndView("html/forgot-password");
			mv.addObject("staticUrl", company.getStaticUrl());
			mv.addObject("message", "Invalid reset password request");
			mv.addObject("isError", true);
			return mv;
		}
		Date dayAfterCreation = DateUtil.addDays(resetPassword.getCreated(), 1);
		if (dayAfterCreation.compareTo(new Date()) <= 0) {
			ModelAndView mv = new ModelAndView("html/forgot-password");
			mv.addObject("staticUrl", company.getStaticUrl());
			mv.addObject("message", "Reset link has expired");
			mv.addObject("isError", true);
			return mv;
		}
		if (ResetStatus.SUCCESS.equals(resetPassword.getStatus())) {
			ModelAndView mv = new ModelAndView("html/forgot-password");
			mv.addObject("staticUrl", company.getStaticUrl());
			mv.addObject("message", "Reset link already used");
			mv.addObject("isError", true);
			return mv;
		}
		return null;
	}

}
