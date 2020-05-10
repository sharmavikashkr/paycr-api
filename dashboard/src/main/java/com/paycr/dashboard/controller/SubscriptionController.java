package com.paycr.dashboard.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.paycr.common.bean.OfflineSubscription;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.SubscriptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

	@Autowired
	private SubscriptionService subsSer;

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@GetMapping("/get/{pricingId}")
	public Subscription getSubscription(@PathVariable Integer pricingId) {
		return subsSer.getSubscription(pricingId);
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@PostMapping("/new/offline")
	public void offlineSubscription(@RequestBody OfflineSubscription offline) {
		subsSer.offlineSubscription(offline);
	}

	/*@RequestMapping(value = "/new/online", method = RequestMethod.GET)
	public ModelAndView onlineSubscription(@RequestParam("access_token") String accessToken,
			@RequestParam("pricing_id") Integer pricingId, @RequestParam("quantity") Integer quantity,
			HttpServletRequest request) {
		Merchant merchant = secSer.getMerchantForLoggedInUser(accessToken);
		try {
			return subsSer.onlineSubscription(pricingId, quantity, merchant);
		} catch (Exception ex) {
			String message = (ex instanceof PaycrException) ? ex.getMessage() : "Resource not found";
			ModelAndView mv = new ModelAndView("html/errorpage");
			mv.addObject("staticUrl", company.getStaticUrl());
			mv.addObject("webUrl", company.getWebUrl());
			mv.addObject("message", message);
			return mv;
		}
	}*/

	@PostMapping("/return")
	public void purchase(@RequestParam Map<String, String> formData, HttpServletResponse response) throws Exception {
		Subscription subs = subsSer.purchase(formData);
		response.sendRedirect("/subscription/response/" + subs.getSubscriptionCode());
	}

	@GetMapping("/decline/{subscriptionCode}")
	public void decline(@PathVariable String subscriptionCode, HttpServletResponse response) throws IOException {
		subsSer.decline(subscriptionCode);
		response.sendRedirect("/subscription/response/" + subscriptionCode);
	}

}
