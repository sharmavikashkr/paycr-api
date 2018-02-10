package com.paycr.expense.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.ExpenseNote;
import com.paycr.common.validation.RequestValidator;

@Service
public class ExpenseNoteValidator implements RequestValidator<ExpenseNote> {

	@Autowired
	private List<RequestValidator<ExpenseNote>> rules;

	@Override
	public void validate(ExpenseNote note) {
		for (RequestValidator<ExpenseNote> validator : rules) {
			validator.validate(note);
		}
	}

}
