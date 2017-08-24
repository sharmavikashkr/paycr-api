package com.paycr.dashboard.controller;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.Constants;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.UserService;

@RestController
@PreAuthorize(RoleUtil.ALL_AUTH)
@RequestMapping("/profile")
public class ProfileController {

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService secSer;

	@RequestMapping("/update/address")
	public void updateAddress(@RequestBody Address address, HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			user.setAddress(address);
			userService.saveUser(user);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@RequestMapping(value = "/change/password", method = RequestMethod.POST)
	public void changePassword(@RequestParam(value = "oldPass", required = true) String oldPass,
			@RequestParam(value = "newPass", required = true) String newPass,
			@RequestParam(value = "retypePass", required = true) String retypePass, HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			if (!newPass.equals(retypePass)) {
				throw new PaycrException(Constants.FAILURE, "Wrong Password Retyped");
			}
			if (bcPassEncode.matches(oldPass, user.getPassword())) {
				user.setPassword(bcPassEncode.encode(newPass));
				userService.saveUser(user);
			} else {
				throw new PaycrException(Constants.FAILURE, "Wrong Password Entered");
			}
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

}
