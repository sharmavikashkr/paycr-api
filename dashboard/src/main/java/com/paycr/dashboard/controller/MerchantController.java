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

import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.service.SecurityService;
import com.paycr.dashboard.service.MerchantService;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private MerchantService merSer;

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
		ModelAndView mv = new ModelAndView("html/dashboard/merchant");
		return mv;
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER')")
	@RequestMapping("/get")
	public Merchant getMerchant() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return merchant;
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER')")
	@RequestMapping("/account/update")
	public Merchant updateAccount(@RequestBody Merchant mer, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merSer.updateAccount(merchant, mer);
			return getMerchant();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/paymentsetting/update")
	public PaymentSetting updatePaymentSetting(@RequestBody PaymentSetting paymentSetting,
			HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merSer.updatePaymentSetting(merchant, paymentSetting);
			return merchant.getPaymentSetting();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/invoicesetting/update")
	public InvoiceSetting updateInvoiceSetting(@RequestBody InvoiceSetting invoiceSetting,
			HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merSer.updateInvoiceSetting(merchant, invoiceSetting);
			return merchant.getInvoiceSetting();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/customParam/new")
	public InvoiceSetting newCustomParam(@RequestBody MerchantCustomParam customParam, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merSer.newCustomParam(merchant, customParam);
			return merchant.getInvoiceSetting();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT')")
	@RequestMapping("/customParam/delete/{id}")
	public InvoiceSetting deleteCustomParam(@PathVariable Integer id, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merSer.deleteCustomParam(merchant, id);
			return merchant.getInvoiceSetting();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}

}
