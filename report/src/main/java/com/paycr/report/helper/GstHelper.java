package com.paycr.report.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr1B2CSmall;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.util.CommonUtil;

@Component
public class GstHelper {

	@Autowired
	private TaxMasterRepository taxMRepo;

	public List<TaxAmount> getTaxAmount(List<InvoiceItem> items) {
		List<TaxAmount> taxes = new ArrayList<>();
		for (InvoiceItem item : items) {
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
						.add(item.getInventory().getRate().multiply(BigDecimal.valueOf(item.getQuantity()))
								.multiply(BigDecimal.valueOf(itemTax.getValue())).divide(BigDecimal.valueOf(100))
								.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}
		}
		return taxes;
	}

	public List<Gstr1B2CSmall> getTaxBreakup(List<InvoiceItem> items) {
		List<Gstr1B2CSmall> b2cSmallList = new ArrayList<>();
		for (InvoiceItem item : items) {
			TaxMaster tax = item.getTax();
			Gstr1B2CSmall b2cSmallInv = null;
			List<Gstr1B2CSmall> exstB2CSmallFt = b2cSmallList.stream().filter(t -> (t.getGstRate() == tax.getValue()))
					.collect(Collectors.toList());
			if (CommonUtil.isEmpty(exstB2CSmallFt)) {
				b2cSmallInv = new Gstr1B2CSmall();
				b2cSmallInv.setGstRate(tax.getValue());
				b2cSmallInv.setCgstAmount(BigDecimal.ZERO);
				b2cSmallInv.setIgstAmount(BigDecimal.ZERO);
				b2cSmallInv.setSgstAmount(BigDecimal.ZERO);
				b2cSmallInv.setTaxableAmount(BigDecimal.ZERO);
				b2cSmallList.add(b2cSmallInv);
			} else {
				b2cSmallInv = exstB2CSmallFt.get(0);
			}
			b2cSmallInv.setTaxableAmount(b2cSmallInv.getTaxableAmount()
					.add(item.getInventory().getRate().multiply(BigDecimal.valueOf(item.getQuantity()))));
			List<TaxMaster> itemTaxes = new ArrayList<>();
			List<TaxMaster> childTaxes = taxMRepo.findByParent(tax);
			if (CommonUtil.isEmpty(childTaxes)) {
				itemTaxes.add(tax);
			} else {
				itemTaxes.addAll(childTaxes);
			}
			for (TaxMaster itemTax : itemTaxes) {
				BigDecimal taxAmt = item.getInventory().getRate().multiply(BigDecimal.valueOf(item.getQuantity()))
						.multiply(BigDecimal.valueOf(itemTax.getValue())).divide(BigDecimal.valueOf(100))
						.setScale(2, BigDecimal.ROUND_HALF_UP);
				if (itemTax.getName().equals("SGST")) {
					b2cSmallInv.setSgstAmount(b2cSmallInv.getSgstAmount().add(taxAmt));
				} else if (itemTax.getName().equals("CGST")) {
					b2cSmallInv.setCgstAmount(b2cSmallInv.getCgstAmount().add(taxAmt));
				} else if (itemTax.getName().equals("IGST")) {
					b2cSmallInv.setIgstAmount(b2cSmallInv.getIgstAmount().add(taxAmt));
				}
			}
		}
		return b2cSmallList;
	}

}
