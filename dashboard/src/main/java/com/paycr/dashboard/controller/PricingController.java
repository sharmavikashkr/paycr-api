package com.paycr.dashboard.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.dashboard.validation.PricingValidator;

@RestController
@RequestMapping("/pricing")
public class PricingController {

	@Autowired
	private PricingValidator pricingValidator;

	@Autowired
	private PricingRepository pricingRepo;

	@RequestMapping("/new")
	public String create(@RequestBody Pricing pricing, HttpServletResponse response) {
		try {
			pricingValidator.validate(pricing);
			pricingRepo.save(pricing);
			return "Pricing created";
		} catch (Exception ex) {
			response.setStatus(500);
			return ex.getMessage();
		}
	}

}
