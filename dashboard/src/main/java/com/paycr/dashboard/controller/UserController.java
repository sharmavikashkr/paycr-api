package com.paycr.dashboard.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
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
	@RequestMapping("")
	public PcUser getUser() {
		PcUser user = secSer.findLoggedInUser();
		user.setAccess(userSer.loadAccess(user));
		return user;
	}

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@RequestMapping("/getAll")
	public List<PcUser> getUsers() {
		return userSer.getUsers();
	}

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@RequestMapping("/new")
	public void createUser(@RequestBody PcUser user, HttpServletResponse response) {
		try {
			userSer.createUser(user);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@RequestMapping("/toggle/{userId}")
	public void toggleUser(@PathVariable("userId") Integer userId, HttpServletResponse response) {
		try {
			userSer.toggleUser(userId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/invoices")
	public List<Invoice> myInvoices(HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			return userSer.getMyInvoices(user);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}

}
