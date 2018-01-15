package com.paycr.invoice.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.InvoiceCreditNote;
import com.paycr.common.validation.RequestValidator;

@Service
public class CreditNoteValidator implements RequestValidator<InvoiceCreditNote> {

	@Autowired
	private List<RequestValidator<InvoiceCreditNote>> rules;

	@Override
	public void validate(InvoiceCreditNote creditNote) {
		for (RequestValidator<InvoiceCreditNote> validator : rules) {
			validator.validate(creditNote);
		}
	}

}
