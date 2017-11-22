package com.paycr.dashboard.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.AdminSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private Company company;

	@RequestMapping("")
	public ModelAndView admin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String token = null;
		if (request.getCookies() == null) {
			response.sendRedirect("/adminlogin");
		}
		for (Cookie cookie : request.getCookies()) {
			if ("access_token".equals(cookie.getName())) {
				token = cookie.getValue();
			}
		}
		if (token == null) {
			response.sendRedirect("/adminlogin");
		}
		boolean isAdmin = secSer.isPaycrUser(token);
		if (!isAdmin) {
			response.sendRedirect("/adminlogin");
		}
		ModelAndView mv = new ModelAndView("html/admin/admin");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
	}

	@PreAuthorize(RoleUtil.PAYCR_ADMIN_AUTH)
	@RequestMapping("/setting")
	public AdminSetting getSetting(HttpServletResponse response) {
		try {
			return adminService.getSetting();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}

	@PreAuthorize(RoleUtil.PAYCR_ADMIN_AUTH)
	@RequestMapping("/setting/update")
	public AdminSetting updateSetting(@RequestBody AdminSetting setting, HttpServletResponse response) {
		try {
			adminService.saveSetting(setting);
			return adminService.getSetting();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}

	@PreAuthorize(RoleUtil.PAYCR_ADMIN_AUTH)
	@RequestMapping("/merchant/new")
	public void newMerchant(@RequestBody Merchant merchant, HttpServletResponse response) {
		try {
			adminService.createMerchant(merchant, secSer.findLoggedInUser().getEmail());
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/pricing/new")
	public void createPricing(@RequestBody Pricing pricing, HttpServletResponse response) {
		try {
			adminService.createPricing(pricing);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/pricing/toggle/{pricingId}")
	public void togglePricing(@PathVariable Integer pricingId, HttpServletResponse response) {
		try {
			adminService.togglePricing(pricingId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

}
