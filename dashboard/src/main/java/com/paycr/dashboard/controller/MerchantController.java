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

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.util.DateUtil;
import com.paycr.dashboard.service.MerchantService;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private MerchantService merSer;

	@Autowired
	private NotificationRepository notiRepo;

	@RequestMapping("")
	public ModelAndView dashboard(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
			response.sendRedirect("/login");
		}
		Merchant merchant = secSer.getMerchantForLoggedInUser(token);
		if (merchant == null) {
			response.sendRedirect("/login");
		}
		PcUser user = secSer.findLoggedInUser(token);
		ModelAndView mv = new ModelAndView("html/dashboard");
		mv.addObject("user", user);
		mv.addObject("providers", ParamValueProvider.values());
		return mv;
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/get")
	public Merchant getMerchant() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		for (MerchantPricing merPri : merchant.getPricings()) {
			merPri.setInvNo(merPri.getInvoices().size());
		}
		return merchant;
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/account/update")
	public Merchant updateAccount(@RequestBody Merchant mer, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merSer.updateAccount(merchant, mer);
			return getMerchant();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/notifications")
	public List<Notification> getNotifications() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		Pageable topFour = new PageRequest(0, 4);
		List<Notification> notices = notiRepo.findByUserIdOrMerchantIdOrderByIdDesc(null, merchant.getId(), topFour);
		for (Notification notice : notices) {
			notice.setCreatedStr(DateUtil.getDashboardDate(notice.getCreated()));
		}
		return notices;
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/setting/update")
	public MerchantSetting updateSetting(@RequestBody MerchantSetting setting, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merSer.updateSetting(merchant, setting);
			return merchant.getSetting();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/customParam/new")
	public MerchantSetting newCustomParam(@RequestBody MerchantCustomParam customParam, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merSer.newCustomParam(merchant, customParam);
			return merchant.getSetting();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/customParam/delete/{id}")
	public MerchantSetting deleteCustomParam(@PathVariable Integer id, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merSer.deleteCustomParam(merchant, id);
			return merchant.getSetting();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/invoices")
	public List<Invoice> myInvoices(HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			return merSer.myInvoices(merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return null;
		}
	}

}
