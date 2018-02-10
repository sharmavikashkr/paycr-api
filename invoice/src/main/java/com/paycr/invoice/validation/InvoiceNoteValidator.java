package com.paycr.invoice.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.validation.RequestValidator;

@Service
public class InvoiceNoteValidator implements RequestValidator<InvoiceNote> {

	@Autowired
	private List<RequestValidator<InvoiceNote>> rules;

	@Override
	public void validate(InvoiceNote note) {
		for (RequestValidator<InvoiceNote> validator : rules) {
			validator.validate(note);
		}
	}

}
