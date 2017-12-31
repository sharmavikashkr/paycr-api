package com.paycr.common.bean;

import java.math.BigDecimal;

public class AssetStats {

	private BigDecimal paidSum;
	private BigDecimal unpaidSum;
	private Long paidNo;
	private Long unpaidNo;

	public BigDecimal getPaidSum() {
		return paidSum;
	}

	public void setPaidSum(BigDecimal paidSum) {
		this.paidSum = paidSum;
	}

	public BigDecimal getUnpaidSum() {
		return unpaidSum;
	}

	public void setUnpaidSum(BigDecimal unpaidSum) {
		this.unpaidSum = unpaidSum;
	}

	public Long getPaidNo() {
		return paidNo;
	}

	public void setPaidNo(Long paidNo) {
		this.paidNo = paidNo;
	}

	public Long getUnpaidNo() {
		return unpaidNo;
	}

	public void setUnpaidNo(Long unpaidNo) {
		this.unpaidNo = unpaidNo;
	}

}
