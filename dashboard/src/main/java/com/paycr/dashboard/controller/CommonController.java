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
import com.paycr.common.service.UserRoleService;
import com.paycr.dashboard.service.CommonService;

@RestController
@RequestMapping("/common")
public class CommonController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private UserRoleService urSer;

	@Autowired
	private CommonService comSer;

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/user")
	public PcUser getUser() {
		PcUser user = secSer.findLoggedInUser();
		user.setPassword("");
		return user;
	}
	
	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/notifications")
	public List<Notification> getNotifications() {
		return comSer.getNotifications();
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/pricings")
	public List<Pricing> getPricings() {
		return comSer.getPricings();
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/roles")
	public String[] getRoles() {
		PcUser user = secSer.findLoggedInUser();
		return urSer.getUserRoles(user);
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/users")
	public List<PcUser> getUsers() {
		return comSer.getUsers();
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/create/user")
	public void createUser(@RequestBody PcUser user, HttpServletResponse response) {
		try {
			comSer.createUser(user);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/toggle/user/{userId}")
	public void toggleUser(@PathVariable("userId") Integer userId) {
		try {
			comSer.toggleUser(userId);
		} catch (Exception ex) {
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
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
