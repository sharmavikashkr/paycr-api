package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.repository.PricingRepository;

@RestController
@RequestMapping("/common")
public class CommonController {

	@Autowired
	private PricingRepository priceRepo;

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/pricings")
	public List<Pricing> getPricings() {
		List<Pricing> pricings = priceRepo.findAll();
		return pricings;
	}
}
