package com.paycr.dashboard.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.dashboard.service.SecureLoginService;

@RestController
public class SecureLoginController {

	@Autowired
	private SecureLoginService secLoginService;

	@RequestMapping(value = "/secure/login", method = RequestMethod.POST)
	public LinkedHashMap secureLogin(@RequestParam("email") String email, @RequestParam("password") String password) {
		return secLoginService.secureLogin(email, password);
	}

}
