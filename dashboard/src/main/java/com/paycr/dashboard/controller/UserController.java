package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private UserService userSer;

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@GetMapping("")
	public PcUser getUser() {
		PcUser user = secSer.findLoggedInUser();
		user.setAccess(userSer.loadAccess(user));
		return user;
	}

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@GetMapping("/getAll")
	public List<PcUser> getUsers() {
		return userSer.getUsers();
	}

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@PostMapping("/new")
	public void createUser(@RequestBody PcUser user) {
		userSer.createUser(user);
	}

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@GetMapping("/toggle/{userId}")
	public void toggleUser(@PathVariable("userId") Integer userId) {
		userSer.toggleUser(userId);
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@GetMapping("/invoices")
	public List<Invoice> myInvoices() {
		PcUser user = secSer.findLoggedInUser();
		return userSer.getMyInvoices(user);
	}

}
