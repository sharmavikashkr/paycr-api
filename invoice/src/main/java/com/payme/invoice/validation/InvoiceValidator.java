package com.payme.invoice.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payme.common.data.domain.Invoice;
import com.payme.common.validation.RequestValidator;

@Service
public class InvoiceValidator implements RequestValidator<Invoice> {

	@Autowired
	private List<RequestValidator<Invoice>> rules;

	@Override
	public void validate(Invoice invoice) {
		for (RequestValidator<Invoice> validator : rules) {
			validator.validate(invoice);
		}
	}

}
