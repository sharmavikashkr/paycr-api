package com.paycr.admin.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;

@RestController
public class AdminLoginController {

	@Autowired
	private Company company;

	@RequestMapping("/adminlogin")
	public ModelAndView adminlogin(HttpServletResponse response) {
		response.addCookie(new Cookie("access_token", ""));
		ModelAndView mv = new ModelAndView("html/adminlogin");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
	}

}
