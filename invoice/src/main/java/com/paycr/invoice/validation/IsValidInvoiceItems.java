package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.repository.InventoryRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ItemType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(2)
public class IsValidInvoiceItems implements RequestValidator<Invoice> {

	@Autowired
	private InventoryRepository invnRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Override
	public void validate(Invoice invoice) {
		if (invoice.isAddItems()) {
			List<InvoiceItem> items = new ArrayList<InvoiceItem>();
			for (InvoiceItem item : invoice.getItems()) {
				validateItem(invoice, item);
				item.setInvoice(invoice);
				items.add(item);
			}
			if (items.size() < 1 || items.size() > 5) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Min 1 and Max 5 Items expected");
			}
			invoice.setItems(items);
		}
	}

	private void validateItem(Invoice invoice, InvoiceItem item) {
		if (CommonUtil.isEmpty(item.getInventory().getName()) || CommonUtil.isNull(item.getInventory().getRate())
				|| CommonUtil.isEmpty(item.getInventory().getCode()) || CommonUtil.isNull(item.getPrice())
				|| 0 == item.getQuantity()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Items entered");
		}
		if (CommonUtil.isNull(item.getTax())) {
			item.setTax(taxMRepo.findByName("NO_TAX"));
		}
		BigDecimal expPrice = item.getInventory().getRate().multiply(BigDecimal.valueOf(item.getQuantity()));
		expPrice = expPrice
				.add(expPrice.multiply(BigDecimal.valueOf(item.getTax().getValue())).divide(BigDecimal.valueOf(100)));
		if (!item.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP)
				.equals(expPrice.setScale(2, BigDecimal.ROUND_HALF_UP))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "rate * quantity != price");
		}
		Inventory inventory = invnRepo.findByMerchantAndCode(invoice.getMerchant(), item.getInventory().getCode());
		if (CommonUtil.isNotNull(inventory)) {
			if (!(inventory.getRate().setScale(2, BigDecimal.ROUND_HALF_UP)
					.compareTo(item.getInventory().getRate()) == 0
					&& inventory.getName().equals(item.getInventory().getName()))) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Mismatch with existing item");
			}
			if (!inventory.isActive()) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Inventory not active");
			}
		} else {
			inventory = new Inventory();
			inventory.setCreated(new Date());
			inventory.setMerchant(invoice.getMerchant());
			inventory.setCode(item.getInventory().getCode());
			inventory.setName(item.getInventory().getName());
			inventory.setRate(item.getInventory().getRate());
			inventory.setDescription(item.getInventory().getDescription());
			inventory.setHsnsac(item.getInventory().getHsnsac());
			inventory.setTax(item.getTax());
			inventory.setCreatedBy(invoice.getCreatedBy());
			inventory.setType(ItemType.SERVICE);
			inventory.setActive(true);
			invnRepo.save(inventory);
		}
		item.setInventory(inventory);
		if (!invoice.isUpdate()) {
			item.setId(null);
		}
	}

}
