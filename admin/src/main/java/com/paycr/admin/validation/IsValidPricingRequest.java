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
				|| CommonUtil.isNull(pricing.getStartAmount()) || CommonUtil.isNull(pricing.getEndAmount())
				|| CommonUtil.isEmpty(pricing.getDescription()) || CommonUtil.isNull(pricing.getRate())
				|| CommonUtil.isNull(pricing.getType()) || pricing.getDuration() <= 0) {
			throw new PaycrException(Constants.FAILURE, "Mandatory params missing");
		}
		if (CommonUtil.isNull(pricing.getTax())) {
			TaxMaster noTax = taxMRepo.findByName("NO_TAX");
			pricing.setTax(noTax);
		}
		pricing.setCreated(new Date());
		pricing.setActive(true);
	}

}
