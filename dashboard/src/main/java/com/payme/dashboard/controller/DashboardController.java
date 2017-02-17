package com.payme.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.payme.common.data.domain.User;

@RestController
public class DashboardController {

	@Autowired
	private SecurityService secSer;

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("html/index");
	}

	@RequestMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("html/login");
	}

	@Secured({ "ROLE_MERCHANT" })
	@RequestMapping("/dashboard")
	public ModelAndView dashboard() {
		User user = secSer.findLoggedInUser();
		ModelAndView mv = new ModelAndView("html/dashboard");
		mv.addObject("name", user.getName());
		return mv;
	}
	
	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/admin")
	public ModelAndView admin() {
		User user = secSer.findLoggedInUser();
		ModelAndView mv = new ModelAndView("html/blank");
		mv.addObject("name", user.getName());
		return mv;
	}
}
