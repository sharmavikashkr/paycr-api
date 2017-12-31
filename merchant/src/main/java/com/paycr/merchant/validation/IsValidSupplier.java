package com.paycr.merchant.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Supplier;
import com.paycr.common.data.repository.SupplierRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidSupplier implements RequestValidator<Supplier> {

	@Autowired
	private SupplierRepository consRepo;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static final String MOBILE_PATTERN = "^[7-9]{1}[0-9]{9}$";

	private static final String NAME_PATTERN = "[a-zA-Z ]*";

	@Override
	public void validate(Supplier supplier) {
		if (CommonUtil.isNull(supplier)) {
			throw new PaycrException(Constants.FAILURE, "Invalid Supplier");
		}
		if (!(match(supplier.getEmail(), EMAIL_PATTERN) && match(supplier.getMobile(), MOBILE_PATTERN)
				&& match(supplier.getName(), NAME_PATTERN))) {
			throw new PaycrException(Constants.FAILURE, "Invalid values of params");
		}
		Supplier extSupplier = consRepo.findSupplierForMerchant(supplier.getMerchant(), supplier.getEmail(),
				supplier.getMobile());
		if (CommonUtil.isNotNull(extSupplier)) {
			throw new PaycrException(Constants.FAILURE, "Supplier already exists");
		}
	}

	private boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

}
