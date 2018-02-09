package com.paycr.admin.validation;

import java.util.Date;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.PricingRule;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidPricingRequest implements RequestValidator<Pricing> {

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Autowired
	private PricingRepository pricingRepo;

	@Override
	public void validate(Pricing pricing) {
		if (CommonUtil.isNull(pricing)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid create pricing request");
		}
		if (CommonUtil.isEmpty(pricing.getCode()) || CommonUtil.isEmpty(pricing.getName())
				|| CommonUtil.isEmpty(pricing.getDescription()) || CommonUtil.isNull(pricing.getType())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Mandatory params missing");
		}
		if (CommonUtil.isNotNull(pricingRepo.findByCode(pricing.getCode()))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Code already used");
		}
		if (pricing.getDuration() < 50 || pricing.getLimit() < 500
				|| (pricing.getLimit() / pricing.getDuration() < 10)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST,
					"Duration must be greater than 50 days and limit/duration must be greater than 10");
		}
		if (CommonUtil.isNull(pricing.getInterstateTax())) {
			TaxMaster noTax = taxMRepo.findByName("NO_TAX");
			pricing.setInterstateTax(noTax);
		}
		if (CommonUtil.isNull(pricing.getIntrastateTax())) {
			TaxMaster noTax = taxMRepo.findByName("NO_TAX");
			pricing.setIntrastateTax(noTax);
		}
		pricing.setRate(PricingRule.getPricingRate(pricing.getLimit(), pricing.getDuration()));
		pricing.setCreated(new Date());
		pricing.setActive(true);
	}

}
