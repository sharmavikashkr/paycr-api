package com.paycr.merchant.validation;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Supplier;
import com.paycr.common.data.repository.SupplierRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidSupplier implements RequestValidator<Supplier> {

	@Autowired
	private SupplierRepository consRepo;

	@Override
	public void validate(Supplier supplier) {
		if (CommonUtil.isNull(supplier)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Supplier");
		}
		if (!(CommonUtil.match(supplier.getEmail(), CommonUtil.EMAIL_PATTERN)
				&& CommonUtil.match(supplier.getMobile(), CommonUtil.MOBILE_PATTERN)
				&& CommonUtil.match(supplier.getName(), CommonUtil.NAME_PATTERN))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid values of params");
		}
		Supplier extSupplier = consRepo.findSupplierForMerchant(supplier.getMerchant(), supplier.getEmail(),
				supplier.getMobile());
		if (CommonUtil.isNotNull(extSupplier)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Supplier already exists");
		}
	}

}
