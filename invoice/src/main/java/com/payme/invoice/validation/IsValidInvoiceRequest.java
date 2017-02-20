package com.payme.invoice.validation;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.payme.common.data.domain.Invoice;
import com.payme.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidInvoiceRequest implements RequestValidator<Invoice> {

	@Override
	public void validate(Invoice invoice) {
		// TODO : Add validation
	}

}
