package com.paycr.dashboard.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.PayMode;
import com.paycr.common.util.DateUtil;
import com.paycr.dashboard.service.AdminService;
import com.paycr.dashboard.validation.MerchantValidator;
import com.paycr.dashboard.validation.PricingValidator;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private AdminService adminService;

	@Autowired
	private MerchantValidator merValidator;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private PricingValidator pricingValidator;

	@Autowired
	private PricingRepository pricingRepo;

	@RequestMapping("")
	public ModelAndView admin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String token = null;
		if (request.getCookies() == null) {
			response.sendRedirect("/login");
		}
		for (Cookie cookie : request.getCookies()) {
			if ("access_token".equals(cookie.getName())) {
				token = cookie.getValue();
			}
		}
		if (token == null) {
			response.sendRedirect("/adminlogin");
		}
		boolean isAdmin = secSer.isLoggedInUserAdmin(token);
		if (!isAdmin) {
			response.sendRedirect("/adminlogin");
		}
		PcUser user = secSer.findLoggedInUser(token);
		ModelAndView mv = new ModelAndView("html/admin");
		mv.addObject("user", user);
		mv.addObject("payModes", PayMode.values());
		return mv;
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/notifications")
	public List<Notification> getNotifications() {
		PcUser user = secSer.findLoggedInUser();
		Pageable topFour = new PageRequest(0, 4);
		List<Notification> notices = notiRepo.findByUserIdOrMerchantIdOrderByIdDesc(user.getId(), null, topFour);
		for (Notification notice : notices) {
			notice.setCreatedStr(DateUtil.getDashboardDate(notice.getCreated()));
		}
		return notices;
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/merchant/new")
	public void newMerchant(@RequestBody Merchant merchant, HttpServletResponse response) {
		try {
			merValidator.validate(merchant);
			adminService.createMerchant(merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/pricing/new")
	public void createPricing(@RequestBody Pricing pricing, HttpServletResponse response) {
		try {
			pricingValidator.validate(pricing);
			pricingRepo.save(pricing);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/pricing/toggle/{pricingId}")
	public void togglePricing(@PathVariable Integer pricingId, HttpServletResponse response) {
		try {
			Pricing pri = pricingRepo.findOne(pricingId);
			if (pri.isActive()) {
				pri.setActive(false);
			} else {
				pri.setActive(true);
			}
			pricingRepo.save(pri);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

}
