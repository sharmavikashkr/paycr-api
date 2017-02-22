package com.payme.invoice.validation;

import java.util.Date;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.payme.common.data.domain.Invoice;
import com.payme.common.exception.PaymeException;
import com.payme.common.util.CommonUtil;
import com.payme.common.util.Constants;
import com.payme.common.validation.RequestValidator;

@Component
@Order(1)
public class IsValidInvoiceConsumer implements RequestValidator<Invoice> {

	@Override
	public void validate(Invoice invoice) {
		if (CommonUtil.isNull(invoice.getConsumer())) {
			throw new PaymeException(Constants.FAILURE, "Invalid Consumer");
		}
		if (CommonUtil.isEmpty(invoice.getConsumer().getEmail())) {
			throw new PaymeException(Constants.FAILURE, "Invalid Consumer Email");
		}
		if (CommonUtil.isEmpty(invoice.getConsumer().getName())) {
			throw new PaymeException(Constants.FAILURE, "Invalid Consumer Name");
		}
		if (CommonUtil.isEmpty(invoice.getConsumer().getMobile())) {
			throw new PaymeException(Constants.FAILURE, "Invalid Consumer Mobile");
		}
		invoice.getConsumer().setActive(true);
		invoice.getConsumer().setCreated(new Date());
		invoice.getConsumer().setInvoice(invoice);
	}

}
