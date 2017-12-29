package com.paycr.expense.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.validation.RequestValidator;

@Service
public class ExpenseValidator implements RequestValidator<Expense> {

	@Autowired
	private List<RequestValidator<Expense>> rules;

	@Override
	public void validate(Expense expense) {
		for (RequestValidator<Expense> validator : rules) {
			validator.validate(expense);
		}
	}

}
