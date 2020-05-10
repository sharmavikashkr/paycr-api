package com.paycr.admin.controller;

import java.util.List;

import com.paycr.admin.service.AdminService;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.RegisterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private SecurityService secSer;

	@Autowired
	private AdminService adminService;

	@Autowired
	private RegisterService registerService;

	@PreAuthorize(RoleUtil.PAYCR_AUTH)
	@GetMapping("/check")
	public void check() {
		logger.info("Check if admin");
	}

	@PreAuthorize(RoleUtil.PAYCR_ADMIN_AUTH)
	@PostMapping("/setting/tax/new")
	public void newTaxMaster(@RequestBody TaxMaster tax) {
		adminService.newTaxMaster(tax);
	}

	@PreAuthorize(RoleUtil.PAYCR_ADMIN_AUTH)
	@PostMapping("/merchant/new")
	public void newMerchant(@RequestBody Merchant merchant) {
		registerService.createMerchant(merchant, secSer.findLoggedInUser().getEmail());
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@PostMapping("/pricing/new")
	public void createPricing(@RequestBody Pricing pricing) {
		adminService.createPricing(pricing);
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@GetMapping("/pricing/toggle/{pricingId}")
	public void togglePricing(@PathVariable Integer pricingId) {
		adminService.togglePricing(pricingId);
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@PutMapping(value = "/pricing/merchant/add")
	public void addPricingMerchant(@RequestParam("pricingId") Integer pricingId,
			@RequestParam("merchantId") Integer merchantId) {
		adminService.addPricingMerchant(pricingId, merchantId);
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@GetMapping("/pricing/merchants/{pricingId}")
	public List<Merchant> getPricingMerchant(@PathVariable Integer pricingId) {
		return adminService.getMerchantForPricing(pricingId);
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@PutMapping("/pricing/merchant/remove")
	public void removePricingMerchant(@RequestParam("pricingId") Integer pricingId,
			@RequestParam("merchantId") Integer merchantId) {
		adminService.removePricingMerchant(pricingId, merchantId);
	}

}
