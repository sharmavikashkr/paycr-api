package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(3)
public class IsValidInvoiceAmount implements RequestValidator<Invoice> {

	DecimalFormat df = new DecimalFormat("#.00");

	@Override
	public void validate(Invoice invoice) {
		if (CommonUtil.isNull(invoice.getPayAmount())) {
			throw new PaycrException(Constants.FAILURE, "Amount cannot be null or blank");
		}
		if (invoice.getPayAmount().compareTo(new BigDecimal(0)) <= 0) {
			throw new PaycrException(Constants.FAILURE, "Amount should be greated than 0");
		}
		if (invoice.isAddItems()) {
			BigDecimal totalRate = new BigDecimal(0);
			BigDecimal totalPrice = new BigDecimal(0);
			for (InvoiceItem item : invoice.getItems()) {
				totalPrice = totalPrice.add(item.getPrice()).setScale(2, BigDecimal.ROUND_UP);
				totalRate = totalRate.add(item.getInventory().getRate().multiply(new BigDecimal(item.getQuantity())))
						.setScale(2, BigDecimal.ROUND_UP);
			}
			if (totalRate.compareTo(invoice.getTotal().setScale(2, BigDecimal.ROUND_UP)) != 0
					|| totalPrice.compareTo(invoice.getTotalPrice().setScale(2, BigDecimal.ROUND_UP)) != 0) {
				throw new PaycrException(Constants.FAILURE, "Items do not amount to total");
			}
		}
		BigDecimal finalAmount = invoice.getTotalPrice().add(invoice.getShipping()).subtract(invoice.getDiscount());
		if (finalAmount.setScale(2, BigDecimal.ROUND_UP).compareTo(invoice.getPayAmount()) != 0) {
			throw new PaycrException(Constants.FAILURE, "Amount calculation mismatch");
		}
	}

}
