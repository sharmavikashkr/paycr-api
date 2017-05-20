package com.paycr.dashboard.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.paycr.common.util.DateUtil;
import com.paycr.dashboard.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private ResetPasswordRepository resetRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationRepository notiRepo;

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("html/index");
	}

	@RequestMapping("/login")
	public ModelAndView login(HttpServletResponse response) {
		response.addCookie(new Cookie("access_token", ""));
		ModelAndView mv = new ModelAndView("html/login");
		return mv;
	}

	@RequestMapping("/adminlogin")
	public ModelAndView adminlogin(HttpServletResponse response) {
		response.addCookie(new Cookie("access_token", ""));
		ModelAndView mv = new ModelAndView("html/adminlogin");
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
		mv.addObject("message", message);
		mv.addObject("isError", isError);
		return mv;
	}

	@RequestMapping(value = "/user/admin", method = RequestMethod.GET)
	public void createUser(HttpServletResponse response) throws IOException {
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

	@RequestMapping(value = "/sendResetPassword", method = RequestMethod.POST)
	public void sendResetPassword(@RequestParam("email") String userEmail, HttpServletResponse response)
			throws IOException {
		Date timeNow = new Date();
		PcUser user = userRepo.findByEmail(userEmail);
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
		PcUser user = userRepo.findByEmail(resetPassword.getEmail());
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
