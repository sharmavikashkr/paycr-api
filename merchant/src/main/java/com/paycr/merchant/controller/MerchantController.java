package com.paycr.merchant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
	@GetMapping("/check")
	public void check() {
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@GetMapping("/get")
	public Merchant getMerchant() {
		return secSer.getMerchantForLoggedInUser();
	}

	@PreAuthorize(RoleUtil.MERCHANT_ADMIN_AUTH)
	@PutMapping("/account/update")
	public Merchant updateAccount(@RequestParam String name, @RequestParam String gstin) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updateAccount(merchant, name, gstin);
		return getMerchant();
	}

	@PreAuthorize(RoleUtil.MERCHANT_ADMIN_AUTH)
	@PutMapping("/address/update")
	public Merchant updateAddress(@Valid @RequestBody Address addr) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updateAddress(merchant, addr);
		return getMerchant();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PutMapping("/paymentsetting/update")
	public PaymentSetting updatePaymentSetting(@RequestBody PaymentSetting paymentSetting) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updatePaymentSetting(merchant, paymentSetting);
		return merchant.getPaymentSetting();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PutMapping("/invoicesetting/update")
	public InvoiceSetting updateInvoiceSetting(@RequestBody InvoiceSetting invoiceSetting) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updateInvoiceSetting(merchant, invoiceSetting);
		return merchant.getInvoiceSetting();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PutMapping("/gstsetting/update")
	public GstSetting updateGstSetting(@RequestBody GstSetting gstSetting) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.updateGstSetting(merchant, gstSetting);
		return merchant.getGstSetting();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PostMapping("/customParam/new")
	public InvoiceSetting newCustomParam(@RequestBody MerchantCustomParam customParam) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.newCustomParam(merchant, customParam);
		return merchant.getInvoiceSetting();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@DeleteMapping("/customParam/delete/{id}")
	public InvoiceSetting deleteCustomParam(@PathVariable Integer id) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		merSer.deleteCustomParam(merchant, id);
		return merchant.getInvoiceSetting();
	}

}
