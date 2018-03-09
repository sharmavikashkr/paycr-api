package com.paycr.merchant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/check")
	public void check() {
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
