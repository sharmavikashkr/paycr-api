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
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.CommonService;

@RestController
@RequestMapping("/common")
public class CommonController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private CommonService comSer;

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/user")
	public PcUser getUser() {
		PcUser user = secSer.findLoggedInUser();
		user.setAccess(comSer.loadAccess(user));
		return user;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/notifications")
	public List<Notification> getNotifications() {
		return comSer.getNotifications();
	}

	@PreAuthorize(RoleUtil.ALL_FINANCE_AUTH)
	@RequestMapping("/pricings")
	public List<Pricing> getPricings() {
		return comSer.getPricings();
	}

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@RequestMapping("/users")
	public List<PcUser> getUsers() {
		return comSer.getUsers();
	}

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@RequestMapping("/create/user")
	public void createUser(@RequestBody PcUser user, HttpServletResponse response) {
		try {
			comSer.createUser(user);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@RequestMapping("/toggle/user/{userId}")
	public void toggleUser(@PathVariable("userId") Integer userId, HttpServletResponse response) {
		try {
			comSer.toggleUser(userId);
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
			return comSer.getMyInvoices(user);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}

}
