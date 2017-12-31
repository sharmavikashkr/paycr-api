package com.paycr.merchant.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Supplier;
import com.paycr.common.validation.RequestValidator;

@Service
public class SupplierValidator implements RequestValidator<Supplier> {

	@Autowired
	private List<RequestValidator<Supplier>> rules;

	@Override
	public void validate(Supplier supplier) {
		for (RequestValidator<Supplier> rule : rules) {
			rule.validate(supplier);
		}
	}

}
