package com.paycr.common.bean;

import java.math.BigDecimal;

import com.paycr.common.data.domain.TaxMaster;

public class TaxAmount {

	private TaxMaster tax;

	private BigDecimal amount;

	public TaxMaster getTax() {
		return tax;
	}

	public void setTax(TaxMaster tax) {
		this.tax = tax;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
