package com.paycr.expense.validation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.Supplier;
import com.paycr.common.data.repository.SupplierRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(1)
public class IsValidExpenseSupplier implements RequestValidator<Expense> {

	@Autowired
	private SupplierRepository supRepo;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static final String MOBILE_PATTERN = "^[7-9]{1}[0-9]{9}$";

	private static final String NAME_PATTERN = "[a-zA-Z ]*";

	@Override
	public void validate(Expense expense) {
		Supplier supplier = expense.getSupplier();
		if (CommonUtil.isNull(supplier)) {
			throw new PaycrException(Constants.FAILURE, "Invalid Consumer");
		}
		if (!(match(supplier.getEmail(), EMAIL_PATTERN) || match(supplier.getMobile(), MOBILE_PATTERN)
				|| match(supplier.getName(), NAME_PATTERN))) {
			throw new PaycrException(Constants.FAILURE, "Invalid values of params");
		}
		Supplier exstSupplier = supRepo.findSupplierForMerchant(expense.getMerchant(), supplier.getEmail(),
				supplier.getMobile());
		if (CommonUtil.isNotNull(exstSupplier) && !exstSupplier.isActive()) {
			throw new PaycrException(Constants.FAILURE, "Consumer not active");
		}
		if (CommonUtil.isNull(exstSupplier)) {
			supplier.setMerchant(expense.getMerchant());
			supplier.setActive(true);
			supplier.setCreated(new Date());
			if (supplier.getCreatedBy() == null) {
				supplier.setCreatedBy(expense.getCreatedBy());
			}
			exstSupplier = supRepo.save(supplier);
		}
		expense.setSupplier(exstSupplier);
	}

	private boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

}
