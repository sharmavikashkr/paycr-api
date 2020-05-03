package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.http.HttpStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(2)
public class IsValidInvoiceNoteAmount implements RequestValidator<InvoiceNote> {

	@Override
	public void validate(InvoiceNote note) {
		if (CommonUtil.isNull(note.getPayAmount())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Amount cannot be null or blank");
		}
		if (note.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Amount should be greated than 0");
		}
		BigDecimal totalRate = BigDecimal.ZERO;
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (InvoiceItem item : note.getItems()) {
			totalPrice = totalPrice.add(item.getPrice()).setScale(2, RoundingMode.HALF_UP);
			totalRate = totalRate.add(item.getInventory().getRate().multiply(BigDecimal.valueOf(item.getQuantity())))
					.setScale(2, RoundingMode.HALF_UP);
		}
		if (totalRate.compareTo(note.getTotal().setScale(2, RoundingMode.HALF_UP)) != 0
				|| totalPrice.compareTo(note.getTotalPrice().setScale(2, RoundingMode.HALF_UP)) != 0) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Items do not amount to total");
		}
		BigDecimal finalAmount = note.getTotalPrice().add(note.getAdjustment());
		if (finalAmount.setScale(2, RoundingMode.HALF_UP).compareTo(note.getPayAmount()) != 0) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Amount calculation mismatch");
		}
	}

}
