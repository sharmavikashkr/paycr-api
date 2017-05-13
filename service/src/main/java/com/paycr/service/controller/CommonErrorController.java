package com.paycr.service.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class CommonErrorController implements ErrorController {

	private static final String PATH = "/error";

	@RequestMapping(value = PATH)
	public ModelAndView error(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("html/errorpage");
		mv.addObject("message", "Requested Resource is not found");
		return mv;
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}

}
