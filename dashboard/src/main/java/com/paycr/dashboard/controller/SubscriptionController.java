package com.paycr.dashboard.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.OfflineSubscription;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.domain.SubscriptionMode;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.SubscriptionService;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private SubscriptionService subsSer;

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/modes")
	public List<SubscriptionMode> getSubscriptionModes() {
		return subsSer.getSubscriptionModes();
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/mode/new")
	public void createSubscriptionSetting(@RequestBody SubscriptionMode subsMode, HttpServletResponse response) {
		try {
			subsSer.createSubscriptionSetting(subsMode);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/get/{subscriptionId}")
	public Subscription getSubscription(@PathVariable Integer subscriptionId, HttpServletResponse response) {
		try {
			return subsSer.getSubscription(subscriptionId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/mode/toggle/{modeId}")
	public void toggleSubscriptionSetting(@PathVariable Integer modeId, HttpServletResponse response) {
		try {
			subsSer.toggleSubscriptionSetting(modeId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/new/offline")
	public void offlineSubscription(@RequestBody OfflineSubscription offline, HttpServletResponse response) {
		try {
			subsSer.offlineSubscription(offline);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@RequestMapping(value = "/new/online", method = RequestMethod.GET)
	public ModelAndView onlineSubscription(@RequestParam("access_token") String accessToken,
			@RequestParam("pricing_id") Integer pricingId, @RequestParam("quantity") Integer quantity,
			HttpServletRequest request) {
		Merchant merchant = secSer.getMerchantForLoggedInUser(accessToken);
		if (CommonUtil.isNull(merchant)) {
			throw new PaycrException(Constants.FAILURE, "We do not recognize you");
		}
		return subsSer.onlineSubscription(pricingId, quantity, merchant);
	}

	@RequestMapping(value = "/return", method = RequestMethod.POST)
	public ModelAndView purchase(@RequestParam Map<String, String> formData, HttpServletResponse response)
			throws IOException {
		ModelAndView mv = new ModelAndView("html/subs-response");
		try {
			mv = subsSer.purchase(formData);
		} catch (RazorpayException e) {
			mv.addObject("status", "FAILURE");
			mv.addObject("success", false);
		}
		return mv;
	}

}
