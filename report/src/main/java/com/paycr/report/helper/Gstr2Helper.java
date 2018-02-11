package com.paycr.report.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.data.domain.ExpenseItem;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.util.CommonUtil;

@Component
public class Gstr2Helper {

	@Autowired
	private TaxMasterRepository taxMRepo;

	public List<TaxAmount> getTaxAmount(List<ExpenseItem> items) {
		List<TaxAmount> taxes = new ArrayList<>();
		for (ExpenseItem item : items) {
			List<TaxMaster> itemTaxes = new ArrayList<>();
			TaxMaster tax = item.getTax();
			List<TaxMaster> childTaxes = taxMRepo.findByParent(tax);
			if (CommonUtil.isEmpty(childTaxes)) {
				itemTaxes.add(tax);
			} else {
				itemTaxes.addAll(childTaxes);
			}
			for (TaxMaster itemTax : itemTaxes) {
				TaxAmount taxAmt = null;
				for (TaxAmount taxA : taxes) {
					if (taxA.getTax().getId() == itemTax.getId()) {
						taxAmt = taxA;
						break;
					}
				}
				if (CommonUtil.isNull(taxAmt)) {
					taxAmt = new TaxAmount();
					taxAmt.setTax(itemTax);
					taxAmt.setAmount(BigDecimal.ZERO);
					taxes.add(taxAmt);
				}
				taxAmt.setAmount(taxAmt.getAmount()
						.add(item.getAsset().getRate().multiply(BigDecimal.valueOf(item.getQuantity()))
								.multiply(BigDecimal.valueOf(itemTax.getValue())).divide(BigDecimal.valueOf(100))
								.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}
		}
		return taxes;
	}

}
