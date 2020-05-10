package com.paycr.dashboard.controller;

import com.paycr.common.data.domain.Merchant;
import com.paycr.dashboard.service.RegisterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

	@Autowired
	private RegisterService registerService;

	@PostMapping("/register")
	public void register(@RequestBody Merchant merchant) {
		registerService.createMerchant(merchant, "SELF");
	}

}
