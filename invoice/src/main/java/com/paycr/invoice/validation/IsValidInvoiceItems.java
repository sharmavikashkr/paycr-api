package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Item;
import com.paycr.common.data.repository.InventoryRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(2)
public class IsValidInvoiceItems implements RequestValidator<Invoice> {

	@Autowired
	private InventoryRepository invnRepo;

	@Override
	public void validate(Invoice invoice) {
		if (invoice.isAddItems()) {
			List<Item> items = new ArrayList<Item>();
			for (Item item : invoice.getItems()) {
				validateItem(invoice, item);
				item.setInvoice(invoice);
				items.add(item);
			}
			if (items.size() < 1 || items.size() > 5) {
				throw new PaycrException(Constants.FAILURE, "Min 1 and Max 5 Items expected");
			}
			invoice.setItems(items);
		}
	}

	private void validateItem(Invoice invoice, Item item) {
		if (CommonUtil.isEmpty(item.getInventory().getName()) || CommonUtil.isNull(item.getInventory().getRate())
				|| CommonUtil.isEmpty(item.getInventory().getCode()) || CommonUtil.isNull(item.getPrice())
				|| 0 == item.getQuantity()) {
			throw new PaycrException(Constants.FAILURE, "Invalid Items entered");
		}
		if (!item.getPrice().equals(item.getInventory().getRate().multiply(new BigDecimal(item.getQuantity())))) {
			throw new PaycrException(Constants.FAILURE, "rate * quantity != price");
		}
		Inventory inventory = invnRepo.findByMerchantAndCode(invoice.getMerchant(), item.getInventory().getCode());
		if (CommonUtil.isNotNull(inventory)) {
			if (!(inventory.getRate().equals(item.getInventory().getRate())
					&& inventory.getName().equals(item.getInventory().getName()))) {
				throw new PaycrException(Constants.FAILURE, "Mismatch with existing item");
			}
			if (!inventory.isActive()) {
				throw new PaycrException(Constants.FAILURE, "Inventory not active");
			}
		} else {
			inventory = new Inventory();
			inventory.setCreated(new Date());
			inventory.setMerchant(invoice.getMerchant());
			inventory.setCode(item.getInventory().getCode());
			inventory.setName(item.getInventory().getName());
			inventory.setRate(item.getInventory().getRate());
			inventory.setCreatedBy(invoice.getCreatedBy());
			inventory.setActive(true);
			invnRepo.save(inventory);
		}
		item.setInventory(inventory);
	}

}
