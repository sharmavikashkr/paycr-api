package com.paycr.invoice.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.validation.RequestValidator;

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
