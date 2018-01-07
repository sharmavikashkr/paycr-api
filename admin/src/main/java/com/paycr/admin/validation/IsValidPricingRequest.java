package com.paycr.admin.validation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.PricingRule;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidPricingRequest implements RequestValidator<Pricing> {

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Override
	public void validate(Pricing pricing) {
		if (CommonUtil.isNull(pricing)) {
			throw new PaycrException(Constants.FAILURE, "Invalid create pricing request");
		}
		if (CommonUtil.isEmpty(pricing.getCode()) || CommonUtil.isEmpty(pricing.getName())
				|| CommonUtil.isEmpty(pricing.getDescription()) || CommonUtil.isNull(pricing.getType())) {
			throw new PaycrException(Constants.FAILURE, "Mandatory params missing");
		}
		if (pricing.getDuration() < 50 || pricing.getLimit() < 500
				|| (pricing.getLimit() / pricing.getDuration() < 10)) {
			throw new PaycrException(Constants.FAILURE,
					"Duration must be greater than 50 days and limit/duration must be greater than 10");
		}
		if (CommonUtil.isNull(pricing.getTax())) {
			TaxMaster noTax = taxMRepo.findByName("NO_TAX");
			pricing.setTax(noTax);
		}
		pricing.setRate(PricingRule.getPricingRate(pricing.getLimit(), pricing.getDuration()));
		pricing.setCreated(new Date());
		pricing.setActive(true);
	}

}
