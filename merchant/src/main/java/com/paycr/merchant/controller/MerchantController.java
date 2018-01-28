package com.paycr.merchant.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.GstSetting;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.merchant.service.MerchantService;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private MerchantService merSer;

	@Autowired
	private Company company;

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
		ModelAndView mv = new ModelAndView("html/merchant/merchant");
		mv.addObject("staticUrl", company.getStaticUrl());
		return mv;
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/get")
	public Merchant getMerchant() {
		return secSer.getMerchantForLoggedInUser();
	}

	@PreAuthorize(RoleUtil.MERCHANT_ADMIN_AUTH)
	@RequestMapping("/account/update")
	public Merchant updateAccount(@RequestBody Merchant mer) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updateAccount(merchant, mer);
		return getMerchant();
	}

	@PreAuthorize(RoleUtil.MERCHANT_ADMIN_AUTH)
	@RequestMapping("/address/update")
	public Merchant updateAddress(@RequestBody Address addr) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updateAddress(merchant, addr);
		return getMerchant();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/paymentsetting/update")
	public PaymentSetting updatePaymentSetting(@RequestBody PaymentSetting paymentSetting) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updatePaymentSetting(merchant, paymentSetting);
		return merchant.getPaymentSetting();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/invoicesetting/update")
	public InvoiceSetting updateInvoiceSetting(@RequestBody InvoiceSetting invoiceSetting) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updateInvoiceSetting(merchant, invoiceSetting);
		return merchant.getInvoiceSetting();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/gstsetting/update")
	public GstSetting updateGstSetting(@RequestBody GstSetting gstSetting) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updateGstSetting(merchant, gstSetting);
		return merchant.getGstSetting();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/customParam/new")
	public InvoiceSetting newCustomParam(@RequestBody MerchantCustomParam customParam) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.newCustomParam(merchant, customParam);
		return merchant.getInvoiceSetting();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/customParam/delete/{id}")
	public InvoiceSetting deleteCustomParam(@PathVariable Integer id) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.deleteCustomParam(merchant, id);
		return merchant.getInvoiceSetting();
	}

}
