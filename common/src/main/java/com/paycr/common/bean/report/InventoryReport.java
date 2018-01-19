package com.paycr.common.bean.report;

import java.math.BigInteger;

public class InventoryReport {

	private String code;
	private String name;
	private Double rate;
	private BigInteger saleQuantity;
	private Double saleAmt;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public BigInteger getSaleQuantity() {
		return saleQuantity;
	}

	public void setSaleQuantity(BigInteger saleQuantity) {
		this.saleQuantity = saleQuantity;
	}

	public Double getSaleAmt() {
		return saleAmt;
	}

	public void setSaleAmt(Double saleAmt) {
		this.saleAmt = saleAmt;
	}

}
