package com.payme.dashboard.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.payme.common.data.domain.PmUser;
import com.payme.common.data.domain.ResetPassword;
import com.payme.common.data.domain.UserRole;
import com.payme.common.data.repository.ResetPasswordRepository;
import com.payme.common.data.repository.UserRepository;
import com.payme.common.type.ResetStatus;
import com.payme.common.type.Role;
import com.payme.common.util.CommonUtil;
import com.payme.common.util.DateUtil;
import com.payme.dashboard.service.UserService;

@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private ResetPasswordRepository resetRepo;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void createUser(HttpServletResponse response) throws IOException {
		Date timeNow = new Date();
		if (CommonUtil.isNotNull(userRepo.findByEmail("admin@payme.com"))) {
			return;
		}
		PmUser user = new PmUser();
		user.setCreated(timeNow);
		user.setName("Vikash Kumar");
		user.setEmail("admin@payme.com");
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile("9970197591");
		List<UserRole> userRoles = new ArrayList<UserRole>();
		UserRole userRole = new UserRole();
		userRole.setRole(Role.ROLE_ADMIN);
		userRole.setPmUser(user);
		userRoles.add(userRole);
		user.setUserRoles(userRoles);
		user.setActive(true);
		userRepo.save(user);
	}

	@RequestMapping(value = "/sendResetPassword", method = RequestMethod.POST)
	public void sendResetPassword(@RequestParam("email") String userEmail, HttpServletResponse response)
			throws IOException {
		Date timeNow = new Date();
		PmUser user = userRepo.findByEmail(userEmail);
		if (CommonUtil.isNotNull(user)) {
			Date yesterday = DateUtil.addDays(timeNow, -1);
			if (resetRepo.findResetCount(yesterday, timeNow) >= 3) {
				response.sendRedirect("/forgotPassword?error=2");
			} else {
				userService.sendResetLink(user);
				response.sendRedirect("/login");
			}
		} else {
			response.sendRedirect("/forgotPassword?error=1");
		}
	}

	@RequestMapping("/resetPassword/{resetCode}")
	public ModelAndView resetPasswordGet(@PathVariable String resetCode) {
		ResetPassword resetPassword = resetRepo.findByResetCode(resetCode);
		ModelAndView mvError = validateResetRequest(resetPassword);
		if (CommonUtil.isNotNull(mvError)) {
			return mvError;
		}
		resetPassword.setStatus(ResetStatus.INTITIATED);
		resetRepo.save(resetPassword);
		ModelAndView mv = new ModelAndView("html/reset-password");
		mv.addObject("email", resetPassword.getEmail());
		mv.addObject("resetCode", resetCode);
		return mv;
	}

	@RequestMapping(value = "/resetPassword/{resetCode}", method = RequestMethod.POST)
	public ModelAndView resetPasswordPost(@PathVariable String resetCode, @RequestParam("password") String password) {
		ResetPassword resetPassword = resetRepo.findByResetCode(resetCode);
		ModelAndView mvError = validateResetRequest(resetPassword);
		if (CommonUtil.isNotNull(mvError)) {
			return mvError;
		}
		PmUser user = userRepo.findByEmail(resetPassword.getEmail());
		user.setPassword(bcPassEncode.encode(password));
		userRepo.save(user);
		resetPassword.setStatus(ResetStatus.SUCCESS);
		resetRepo.save(resetPassword);
		return new ModelAndView("html/login");
	}

	private ModelAndView validateResetRequest(ResetPassword resetPassword) {
		if (CommonUtil.isNull(resetPassword)) {
			ModelAndView mv = new ModelAndView("html/forgot-password");
			mv.addObject("message", "Invalid reset password request");
			mv.addObject("isError", true);
			return mv;
		}
		Date dayAfterCreation = DateUtil.addDays(resetPassword.getCreated(), 1);
		if (dayAfterCreation.compareTo(new Date()) <= 0) {
			ModelAndView mv = new ModelAndView("html/forgot-password");
			mv.addObject("message", "Reset link has expired");
			mv.addObject("isError", true);
			return mv;
		}
		if (ResetStatus.SUCCESS.equals(resetPassword.getStatus())) {
			ModelAndView mv = new ModelAndView("html/forgot-password");
			mv.addObject("message", "Reset link already used");
			mv.addObject("isError", true);
			return mv;
		}
		return null;
	}

}
