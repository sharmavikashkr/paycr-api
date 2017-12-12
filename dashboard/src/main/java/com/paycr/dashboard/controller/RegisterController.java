package com.paycr.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.exception.PaycrException;
import com.paycr.dashboard.service.RegisterService;

@RestController
public class RegisterController {

	@Autowired
	private RegisterService registerService;

	@Autowired
	private Company company;

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView mv = new ModelAndView("html/register");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("error", false);
		mv.addObject("errorMessage", "");
		return mv;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView register(Merchant merchant) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("staticUrl", company.getStaticUrl());
		try {
			registerService.createMerchant(merchant, "SELF");
			mv.setViewName("html/register-success");
			mv.addObject("staticUrl", company.getStaticUrl());
			return mv;
		} catch (Exception ex) {
			mv.setViewName("html/register");
			mv.addObject("error", true);
			String errorMessage = "Something went wrong";
			if (ex instanceof PaycrException) {
				errorMessage = ex.getMessage();
			}
			mv.addObject("errorMessage", errorMessage);
		}
		return mv;
	}

}
