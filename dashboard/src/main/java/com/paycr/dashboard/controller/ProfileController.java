package com.paycr.dashboard.controller;

import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.UserService;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize(RoleUtil.ALL_AUTH)
@RequestMapping("/profile")
public class ProfileController {

	@Autowired
	private PasswordEncoder passEncoder;

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@PutMapping("/update/address")
	public void updateAddress(@RequestBody Address address) {
		PcUser user = secSer.findLoggedInUser();
		userService.saveAddress(user, address);
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@PostMapping("/change/password")
	public void changePassword(@RequestParam(value = "oldPass", required = true) String oldPass,
			@RequestParam(value = "newPass", required = true) String newPass,
			@RequestParam(value = "retypePass", required = true) String retypePass) {
		PcUser user = secSer.findLoggedInUser();
		if (!newPass.equals(retypePass)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Wrong Password Retyped");
		}
		if (passEncoder.matches(oldPass, user.getPassword())) {
			user.setPassword(passEncoder.encode(newPass));
			userService.saveUser(user);
		} else {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Wrong Password Entered");
		}
	}

}
