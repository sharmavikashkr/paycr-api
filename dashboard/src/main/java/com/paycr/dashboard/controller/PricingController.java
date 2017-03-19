package com.paycr.dashboard.controller;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
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

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/new")
	public String create(@RequestBody Pricing pricing, HttpServletResponse response) {
		try {
			pricingValidator.validate(pricing);
			pricingRepo.save(pricing);
			return "Pricing created";
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return ex.getMessage();
		}
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/toggle/{pricingId}")
	public String create(@PathVariable Integer pricingId, HttpServletResponse response) {
		try {
			Pricing pri = pricingRepo.findOne(pricingId);
			if (pri.isActive()) {
				pri.setActive(false);
			} else {
				pri.setActive(true);
			}
			pricingRepo.save(pri);
			return "SUCCESS";
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return ex.getMessage();
		}
	}

}
