package com.paycr.dashboard.controller;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.dashboard.service.SecureLoginService;

@RestController
public class SecureLoginController {

	@Autowired
	private SecureLoginService secLoginService;

	@RequestMapping(value = "/secure/login", method = RequestMethod.POST)
	public LinkedHashMap secureLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpServletResponse response) {
		try {
			return secLoginService.secureLogin(email, password);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.UNAUTHORIZED_401);
			throw new PaycrException(Constants.FAILURE, "Invalid credentials");
		}
	}

}
