package com.paycr.dashboard.validation;

import java.util.Date;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Pricing;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidPricingRequest implements RequestValidator<Pricing> {

	@Override
	public void validate(Pricing pricing) {
		if (CommonUtil.isNull(pricing)) {
			throw new PaycrException(Constants.FAILURE, "Invalid create pricing request");
		}
		if (CommonUtil.isEmpty(pricing.getName()) || CommonUtil.isEmpty(pricing.getDescription())
				|| CommonUtil.isNull(pricing.getStartAmount()) || CommonUtil.isNull(pricing.getEndAmount())
				|| CommonUtil.isNull(pricing.getRate()) || pricing.getInvoiceLimit() <= 0
				|| pricing.getDuration() <= 0) {
			throw new PaycrException(Constants.FAILURE, "Mandatory params missing");
		}
		pricing.setCreated(new Date());
		pricing.setActive(true);
	}

}
