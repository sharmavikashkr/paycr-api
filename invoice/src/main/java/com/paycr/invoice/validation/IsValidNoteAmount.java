package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(2)
public class IsValidNoteAmount implements RequestValidator<InvoiceNote> {

	DecimalFormat df = new DecimalFormat("#.00");

	@Override
	public void validate(InvoiceNote note) {
		if (CommonUtil.isNull(note.getPayAmount())) {
			throw new PaycrException(Constants.FAILURE, "Amount cannot be null or blank");
		}
		if (note.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new PaycrException(Constants.FAILURE, "Amount should be greated than 0");
		}
		BigDecimal totalRate = BigDecimal.ZERO;
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (InvoiceItem item : note.getItems()) {
			totalPrice = totalPrice.add(item.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
			totalRate = totalRate.add(item.getInventory().getRate().multiply(BigDecimal.valueOf(item.getQuantity())))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		if (totalRate.compareTo(note.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0
				|| totalPrice.compareTo(note.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0) {
			throw new PaycrException(Constants.FAILURE, "Items do not amount to total");
		}
		BigDecimal finalAmount = note.getTotalPrice().add(note.getAdjustment());
		if (finalAmount.setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(note.getPayAmount()) != 0) {
			throw new PaycrException(Constants.FAILURE, "Amount calculation mismatch");
		}
	}

}
