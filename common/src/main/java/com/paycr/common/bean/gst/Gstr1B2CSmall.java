package com.paycr.common.bean.gst;

import java.math.BigDecimal;

public class Gstr1B2CSmall {

	private float gstRate;
	private BigDecimal taxableAmount;
	private BigDecimal cgstAmount;
	private BigDecimal sgstAmount;
	private BigDecimal igstAmount;

	public float getGstRate() {
		return gstRate;
	}

	public void setGstRate(float gstRate) {
		this.gstRate = gstRate;
	}

	public BigDecimal getTaxableAmount() {
		return taxableAmount;
	}

	public void setTaxableAmount(BigDecimal taxableAmount) {
		this.taxableAmount = taxableAmount;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

}
