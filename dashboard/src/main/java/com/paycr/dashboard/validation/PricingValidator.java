package com.paycr.dashboard.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Pricing;
import com.paycr.common.validation.RequestValidator;

@Service
public class PricingValidator implements RequestValidator<Pricing> {

	@Autowired
	private List<RequestValidator<Pricing>> rules;

	@Override
	public void validate(Pricing pricing) {
		for (RequestValidator<Pricing> rule : rules) {
			rule.validate(pricing);
		}
	}

}
