package com.paycr.merchant.controller;

import java.util.LinkedHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.merchant.service.MerchantLoginService;

@RestController
public class MerchantLoginController {

	@Autowired
	private MerchantLoginService merLoginService;

	@Autowired
	private Company company;

	@RequestMapping("/login")
	public ModelAndView login(HttpServletResponse response) {
		response.addCookie(new Cookie("access_token", ""));
		ModelAndView mv = new ModelAndView("html/login");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
	}

	@RequestMapping(value = "/secure/login", method = RequestMethod.POST)
	public LinkedHashMap secureLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpServletResponse response) {
		try {
			return merLoginService.secureLogin(email, password);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.UNAUTHORIZED_401);
			throw new PaycrException(Constants.FAILURE, "Invalid credentials");
		}
	}

}
