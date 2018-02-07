package com.paycr.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.Merchant;
import com.paycr.dashboard.service.RegisterService;

@RestController
public class RegisterController {

	@Autowired
	private RegisterService registerService;

	@Autowired
	private Company company;

	// @RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView mv = new ModelAndView("html/register");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("error", false);
		mv.addObject("errorMessage", "");
		return mv;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void register(@RequestBody Merchant merchant) {
		registerService.createMerchant(merchant, "SELF");
	}

}
