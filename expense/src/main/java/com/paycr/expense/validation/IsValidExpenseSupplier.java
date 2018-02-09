package com.paycr.expense.validation;

import java.util.Date;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.Supplier;
import com.paycr.common.data.repository.SupplierRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(1)
public class IsValidExpenseSupplier implements RequestValidator<Expense> {

	@Autowired
	private SupplierRepository supRepo;

	@Override
	public void validate(Expense expense) {
		Supplier supplier = expense.getSupplier();
		if (CommonUtil.isNull(supplier)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Consumer");
		}
		if (!(CommonUtil.match(supplier.getEmail(), CommonUtil.EMAIL_PATTERN)
				|| CommonUtil.match(supplier.getMobile(), CommonUtil.MOBILE_PATTERN)
				|| CommonUtil.match(supplier.getName(), CommonUtil.NAME_PATTERN))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid values of params");
		}
		Supplier exstSupplier = supRepo.findSupplierForMerchant(expense.getMerchant(), supplier.getEmail(),
				supplier.getMobile());
		if (CommonUtil.isNotNull(exstSupplier) && !exstSupplier.isActive()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Consumer not active");
		}
		if (CommonUtil.isNull(exstSupplier)) {
			supplier.setMerchant(expense.getMerchant());
			supplier.setActive(true);
			supplier.setCreated(new Date());
			if (CommonUtil.isNull(supplier.getCreatedBy())) {
				supplier.setCreatedBy(expense.getCreatedBy());
			}
			exstSupplier = supRepo.save(supplier);
		}
		expense.setSupplier(exstSupplier);
	}

}
