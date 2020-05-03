package com.paycr.report.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr2Nil;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpenseItem;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.type.SupplyType;
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
								.setScale(2, RoundingMode.HALF_UP)));
				taxAmt.setTaxableAmount(item.getAsset().getRate().multiply(BigDecimal.valueOf(item.getQuantity()))
						.setScale(2, RoundingMode.HALF_UP));
			}
		}
		return taxes;
	}

	public void getSupplyBreakup(Expense expense, List<Gstr2Nil> nilList) {
		List<TaxAmount> taxAmtList = getTaxAmount(expense.getItems());
		for (TaxAmount taxAmt : taxAmtList) {
			SupplyType st = null;
			Gstr2Nil nil = null;
			if (taxAmt.getTax().getName().contains("IGST")) {
				st = SupplyType.INTER;
			} else if (!taxAmt.getTax().getName().contains("IGST")) {
				st = SupplyType.INTRA;
			}
			final SupplyType supType = st;
			List<Gstr2Nil> exstNil = nilList.stream().filter(t -> (supType.equals(t.getSupplyType())))
					.collect(Collectors.toList());
			if (CommonUtil.isEmpty(exstNil)) {
				nil = new Gstr2Nil();
				nil.setSupplyType(supType);
				nil.setExempted(BigDecimal.ZERO);
				nil.setNilRated(BigDecimal.ZERO);
				nil.setNonGst(BigDecimal.ZERO);
				nilList.add(nil);
			} else {
				nil = exstNil.get(0);
			}
			if (taxAmt.getTax().getName().contains("EXEMPTED")) {
				nil.setExempted(nil.getExempted().add(taxAmt.getTaxableAmount()));
			} else if (taxAmt.getTax().getName().contains("NON")) {
				nil.setNonGst(nil.getNonGst().add(taxAmt.getTaxableAmount()));
			} else {
				nil.setNilRated(nil.getNilRated().add(taxAmt.getTaxableAmount()));
			}
		}
	}

}
