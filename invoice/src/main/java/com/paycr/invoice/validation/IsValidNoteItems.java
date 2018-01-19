package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.repository.InventoryRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(1)
public class IsValidNoteItems implements RequestValidator<InvoiceNote> {

	@Autowired
	private InventoryRepository invnRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Override
	public void validate(InvoiceNote note) {
		List<InvoiceItem> items = new ArrayList<InvoiceItem>();
		for (InvoiceItem item : note.getItems()) {
			validateItem(note, item);
			item.setInvoiceNote(note);
			items.add(item);
		}
		if (items.size() < 1 || items.size() > 5) {
			throw new PaycrException(Constants.FAILURE, "Min 1 and Max 5 Items expected");
		}
		note.setItems(items);
	}

	private void validateItem(InvoiceNote note, InvoiceItem item) {
		if (CommonUtil.isEmpty(item.getInventory().getName()) || CommonUtil.isNull(item.getInventory().getRate())
				|| CommonUtil.isEmpty(item.getInventory().getCode()) || CommonUtil.isNull(item.getPrice())
				|| 0 == item.getQuantity()) {
			throw new PaycrException(Constants.FAILURE, "Invalid Items entered");
		}
		if (CommonUtil.isNull(item.getTax())) {
			item.setTax(taxMRepo.findByName("NO_TAX"));
		}
		BigDecimal expPrice = item.getInventory().getRate().multiply(new BigDecimal(item.getQuantity()));
		expPrice = expPrice
				.add(expPrice.multiply(new BigDecimal(item.getTax().getValue())).divide(new BigDecimal(100)));
		if (!item.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP)
				.equals(expPrice.setScale(2, BigDecimal.ROUND_HALF_UP))) {
			throw new PaycrException(Constants.FAILURE, "rate * quantity != price");
		}
		Inventory inventory = invnRepo.findByMerchantAndCode(note.getMerchant(), item.getInventory().getCode());
		if (CommonUtil.isNotNull(inventory)) {
			if (!(inventory.getRate().setScale(2, BigDecimal.ROUND_HALF_UP)
					.compareTo(item.getInventory().getRate()) == 0
					&& inventory.getName().equals(item.getInventory().getName()))) {
				throw new PaycrException(Constants.FAILURE, "Mismatch with existing item");
			}
			if (!inventory.isActive()) {
				throw new PaycrException(Constants.FAILURE, "Inventory not active");
			}
		} else {
			inventory = new Inventory();
			inventory.setCreated(new Date());
			inventory.setMerchant(note.getMerchant());
			inventory.setCode(item.getInventory().getCode());
			inventory.setName(item.getInventory().getName());
			inventory.setRate(item.getInventory().getRate());
			inventory.setTax(item.getTax());
			inventory.setCreatedBy(note.getCreatedBy());
			inventory.setActive(true);
			invnRepo.save(inventory);
		}
		item.setInventory(inventory);
	}

}
