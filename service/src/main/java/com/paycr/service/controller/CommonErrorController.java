package com.paycr.service.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;

@RestController
public class CommonErrorController implements ErrorController {

	private static final String PATH = "/error";

	@Autowired
	private Company company;

	@RequestMapping(value = PATH)
	public ModelAndView error(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("html/errorpage");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("webUrl", company.getWebUrl());
		mv.addObject("message", "Requested Resource not found");
		return mv;
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}

}
