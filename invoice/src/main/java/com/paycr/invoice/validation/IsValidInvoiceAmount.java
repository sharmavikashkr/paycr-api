package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Item;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(3)
public class IsValidInvoiceAmount implements RequestValidator<Invoice> {

	DecimalFormat df = new DecimalFormat("#.00");

	@Override
	public void validate(Invoice invoice) {
		if (invoice.isAddItems()) {
			BigDecimal total = new BigDecimal(0);
			for (Item item : invoice.getItems()) {
				total = total.add(item.getPrice());
			}
			if (!total.equals(invoice.getTotal())) {
				throw new PaycrException(Constants.FAILURE, "Items do not amount to total");
			}
		}
		BigDecimal finalAmount = invoice.getTotal()
				.add(invoice.getTotal().multiply(new BigDecimal(invoice.getTaxValue())).divide(new BigDecimal(100)))
				.subtract(invoice.getDiscount());
		if (finalAmount.setScale(2, BigDecimal.ROUND_UP).compareTo(invoice.getPayAmount()) != 0) {
			throw new PaycrException(Constants.FAILURE, "Amount calculation mismatch");
		}
	}

}
