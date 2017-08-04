package com.paycr.common.bean;

import java.math.BigDecimal;

public class DailyPay implements Comparable<DailyPay> {

	private String created;
	private BigDecimal salePaySum;
	private BigDecimal refundPaySum;

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public BigDecimal getSalePaySum() {
		return salePaySum;
	}

	public void setSalePaySum(BigDecimal salePaySum) {
		this.salePaySum = salePaySum;
	}

	public BigDecimal getRefundPaySum() {
		return refundPaySum;
	}

	public void setRefundPaySum(BigDecimal refundPaySum) {
		this.refundPaySum = refundPaySum;
	}

	@Override
	public int compareTo(DailyPay dp) {
		return created.compareTo(dp.getCreated());
	}

}
