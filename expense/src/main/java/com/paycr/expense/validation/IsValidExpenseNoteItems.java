package com.paycr.expense.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.ExpenseItem;
import com.paycr.common.data.domain.ExpenseNote;
import com.paycr.common.data.repository.AssetRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(1)
public class IsValidExpenseNoteItems implements RequestValidator<ExpenseNote> {

	@Autowired
	private AssetRepository invnRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Override
	public void validate(ExpenseNote note) {
		List<ExpenseItem> items = new ArrayList<ExpenseItem>();
		for (ExpenseItem item : note.getItems()) {
			validateItem(note, item);
			item.setExpenseNote(note);
			items.add(item);
		}
		if (items.size() < 1 || items.size() > 5) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Min 1 and Max 5 Items expected");
		}
		note.setItems(items);
	}

	private void validateItem(ExpenseNote note, ExpenseItem item) {
		if (CommonUtil.isEmpty(item.getAsset().getName()) || CommonUtil.isNull(item.getAsset().getRate())
				|| CommonUtil.isEmpty(item.getAsset().getCode()) || CommonUtil.isNull(item.getPrice())
				|| 0 == item.getQuantity()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Items entered");
		}
		if (CommonUtil.isNull(item.getTax())) {
			item.setTax(taxMRepo.findByName("NO_TAX"));
		}
		BigDecimal expPrice = item.getAsset().getRate().multiply(BigDecimal.valueOf(item.getQuantity()));
		expPrice = expPrice
				.add(expPrice.multiply(BigDecimal.valueOf(item.getTax().getValue())).divide(BigDecimal.valueOf(100)));
		if (!item.getPrice().setScale(2, RoundingMode.HALF_UP).equals(expPrice.setScale(2, RoundingMode.HALF_UP))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "rate * quantity != price");
		}
		Asset asset = invnRepo.findByMerchantAndCode(note.getMerchant(), item.getAsset().getCode());
		if (CommonUtil.isNotNull(asset)) {
			if (!(asset.getRate().setScale(2, RoundingMode.HALF_UP).compareTo(item.getAsset().getRate()) == 0
					&& asset.getName().equals(item.getAsset().getName()))) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Mismatch with existing item");
			}
			if (!asset.isActive()) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Asset not active");
			}
		} else {
			asset = new Asset();
			asset.setCreated(new Date());
			asset.setMerchant(note.getMerchant());
			asset.setCode(item.getAsset().getCode());
			asset.setName(item.getAsset().getName());
			asset.setRate(item.getAsset().getRate());
			asset.setTax(item.getTax());
			asset.setCreatedBy(note.getCreatedBy());
			asset.setActive(true);
			invnRepo.save(asset);
		}
		item.setAsset(asset);
	}

}
