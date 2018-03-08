package com.paycr.common.bean.gst;

import java.math.BigDecimal;

import com.paycr.common.type.SupplyType;

public class Gstr2Nil {

	private SupplyType supplyType;
	private BigDecimal nilRated;
	private BigDecimal exempted;
	private BigDecimal nonGst;

	public BigDecimal getNilRated() {
		return nilRated;
	}

	public void setNilRated(BigDecimal nilRated) {
		this.nilRated = nilRated;
	}

	public BigDecimal getExempted() {
		return exempted;
	}

	public void setExempted(BigDecimal exempted) {
		this.exempted = exempted;
	}

	public BigDecimal getNonGst() {
		return nonGst;
	}

	public void setNonGst(BigDecimal nonGst) {
		this.nonGst = nonGst;
	}

	public SupplyType getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(SupplyType supplyType) {
		this.supplyType = supplyType;
	}

}
