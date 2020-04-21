package com.paycr.dashboard.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.UserCreds;
import com.paycr.dashboard.service.SecureLoginService;

@RestController
public class SecureLoginController {

	@Autowired
	private SecureLoginService secLoginService;

	@PostMapping(value = "/secure/login")
	public LinkedHashMap secureLogin(@RequestBody UserCreds userCreds) {
		return secLoginService.secureLogin(userCreds.getEmail(), userCreds.getPassword());
	}

}
