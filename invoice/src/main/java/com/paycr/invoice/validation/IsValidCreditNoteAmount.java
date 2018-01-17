package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.InvoiceCreditNote;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(2)
public class IsValidCreditNoteAmount implements RequestValidator<InvoiceCreditNote> {

	DecimalFormat df = new DecimalFormat("#.00");

	@Override
	public void validate(InvoiceCreditNote creditNote) {
		if (CommonUtil.isNull(creditNote.getPayAmount())) {
			throw new PaycrException(Constants.FAILURE, "Amount cannot be null or blank");
		}
		if (creditNote.getPayAmount().compareTo(new BigDecimal(0)) <= 0) {
			throw new PaycrException(Constants.FAILURE, "Amount should be greated than 0");
		}
		BigDecimal totalRate = new BigDecimal(0);
		BigDecimal totalPrice = new BigDecimal(0);
		for (InvoiceItem item : creditNote.getItems()) {
			totalPrice = totalPrice.add(item.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
			totalRate = totalRate.add(item.getInventory().getRate().multiply(new BigDecimal(item.getQuantity())))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		if (totalRate.compareTo(creditNote.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0
				|| totalPrice.compareTo(creditNote.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP)) != 0) {
			throw new PaycrException(Constants.FAILURE, "Items do not amount to total");
		}
		BigDecimal finalAmount = creditNote.getTotalPrice().add(creditNote.getAdjustment());
		if (finalAmount.setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(creditNote.getPayAmount()) != 0) {
			throw new PaycrException(Constants.FAILURE, "Amount calculation mismatch");
		}
	}

}
