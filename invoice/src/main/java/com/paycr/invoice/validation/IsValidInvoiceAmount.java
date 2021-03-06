package com.paycr.invoice.validation;

import java.math.BigDecimal;

import org.apache.http.HttpStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(3)
public class IsValidInvoiceAmount implements RequestValidator<Invoice> {

	@Override
	public void validate(Invoice invoice) {
		if (CommonUtil.isNull(invoice.getPayAmount())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Amount cannot be null or blank");
		}
		if (invoice.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Amount should be greated than 0");
		}
		if (invoice.isAddItems()) {
			BigDecimal totalRate = BigDecimal.ZERO;
			BigDecimal totalPrice = BigDecimal.ZERO;
			for (InvoiceItem item : invoice.getItems()) {
				totalPrice = totalPrice.add(item.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
				totalRate = totalRate
						.add(item.getInventory().getRate().multiply(BigDecimal.valueOf(item.getQuantity())))
						.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			if (totalRate.compareTo(invoice.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0
					|| totalPrice.compareTo(invoice.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Items do not amount to total");
			}
		}
		BigDecimal finalAmount = invoice.getTotalPrice().add(invoice.getShipping()).subtract(invoice.getDiscount());
		if (finalAmount.setScale(2, BigDecimal.ROUND_HALF_UP)
				.compareTo(invoice.getPayAmount().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Amount calculation mismatch");
		}
	}

}
