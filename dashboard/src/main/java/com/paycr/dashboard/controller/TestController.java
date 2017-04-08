package com.paycr.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.util.HmacSignerUtil;

@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	private HmacSignerUtil hmacUtil;

	@RequestMapping("/signature")
	public String getSignature(@RequestParam("secret") String secret, @RequestParam("data") String data) {
		return hmacUtil.signWithSecretKey(secret, data);
	}

}
