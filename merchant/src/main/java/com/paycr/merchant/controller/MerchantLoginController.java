package com.paycr.merchant.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;

@RestController
public class MerchantLoginController {

	@Autowired
	private Company company;

	@RequestMapping("/login")
	public ModelAndView login(HttpServletResponse response) {
		response.addCookie(new Cookie("access_token", ""));
		ModelAndView mv = new ModelAndView("html/login");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
	}

}
