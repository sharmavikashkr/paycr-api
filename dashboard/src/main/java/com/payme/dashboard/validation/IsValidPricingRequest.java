package com.payme.dashboard.validation;

import java.util.Date;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.payme.common.data.domain.Pricing;
import com.payme.common.exception.PaymeException;
import com.payme.common.util.CommonUtil;
import com.payme.common.util.Constants;
import com.payme.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidPricingRequest implements RequestValidator<Pricing> {

	@Override
	public void validate(Pricing pricing) {
		if (CommonUtil.isNull(pricing)) {
			throw new PaymeException(Constants.FAILURE, "Invalid create pricing request");
		}
		if (CommonUtil.isEmpty(pricing.getName()) || CommonUtil.isEmpty(pricing.getDescription())
				|| CommonUtil.isNull(pricing.getStartAmount()) || CommonUtil.isNull(pricing.getEndAmount())
				|| CommonUtil.isNull(pricing.getRate()) || pricing.getInvoiceLimit() <= 0
				|| pricing.getDuration() <= 0) {
			throw new PaymeException(Constants.FAILURE, "Mandatory params missing");
		}
		pricing.setCreated(new Date());
		pricing.setActive(true);
	}

}
