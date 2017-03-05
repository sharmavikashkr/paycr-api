package com.paycr.invoice.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Item;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(2)
public class IsValidInvoiceItems implements RequestValidator<Invoice> {

	@Override
	public void validate(Invoice invoice) {
		List<Item> items = new ArrayList<Item>();
		for (Item item : invoice.getItems()) {
			if (validateItem(item)) {
				item.setInvoice(invoice);
				items.add(item);
			}
		}
		if (CommonUtil.isEmpty(items)) {
			throw new PaycrException(Constants.FAILURE, "No Items specified");
		}
		invoice.setItems(items);
	}

	private boolean validateItem(Item item) {
		if ("".equals(item.getName().trim()) || CommonUtil.isNull(item.getRate()) || CommonUtil.isNull(item.getPrice())
				|| 0 == item.getQuantity()) {
			return false;
		} else {
			return true;
		}
	}

}
