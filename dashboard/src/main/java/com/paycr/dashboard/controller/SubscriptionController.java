package com.paycr.dashboard.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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

import com.paycr.common.bean.Company;
import com.paycr.common.bean.OfflineSubscription;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.SubscriptionService;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private SubscriptionService subsSer;

	@Autowired
	private Company company;

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/get/{pricingId}")
	public Subscription getSubscription(@PathVariable Integer pricingId, HttpServletResponse response) {
		try {
			return subsSer.getSubscription(pricingId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
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
	public void purchase(@RequestParam Map<String, String> formData, HttpServletResponse response) throws IOException {
		try {
			Subscription subs = subsSer.purchase(formData);
			response.sendRedirect("/subscription/response/" + subs.getSubscriptionCode());
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@RequestMapping("/decline/{subscriptionCode}")
	public void decline(@PathVariable String subscriptionCode, HttpServletResponse response) throws IOException {
		try {
			subsSer.decline(subscriptionCode);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		response.sendRedirect("/subscription/response/" + subscriptionCode);
	}

	@RequestMapping(value = "/response/{subscriptionCode}", method = RequestMethod.GET)
	public ModelAndView response(@PathVariable String subscriptionCode,
			@RequestParam(value = "show", required = false) Boolean show, HttpServletResponse response)
					throws IOException {
		ModelAndView mv = new ModelAndView("html/subs-response");
		mv.addObject("staticUrl", company.getStaticUrl());
		try {
			Subscription subs = subsSer.getSubscriptionByCode(subscriptionCode);
			mv.addObject("subs", subs);
			show = (show != null) ? show : true;
			mv.addObject("show", show);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return mv;
	}

	@RequestMapping(value = "/receipt/download/{subscriptionCode}", method = RequestMethod.GET)
	public void download(@PathVariable String subscriptionCode, HttpServletResponse response) throws IOException {
		File pdfFile = subsSer.downloadPdf(subscriptionCode);
		response.setContentType("application/pdf");

		FileInputStream fis = null;
		byte[] bFile = new byte[(int) pdfFile.length()];
		fis = new FileInputStream(pdfFile);
		fis.read(bFile);
		fis.close();

		response.setHeader("Content-Disposition",
				"attachment; filename=\"SubscriptionReceipt-" + subscriptionCode + ".pdf\"");
		response.setContentType("application/pdf");
		InputStream is = new ByteArrayInputStream(bFile);
		IOUtils.copy(is, response.getOutputStream());
		response.setContentLength(bFile.length);
		response.flushBuffer();
	}

}
