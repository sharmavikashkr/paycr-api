package com.paycr.common.bean;

public class DailyPay {

	private String created;
	private Double salePaySum;
	private Double refundPaySum;

	public DailyPay() {
		super();
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public Double getSalePaySum() {
		return salePaySum;
	}

	public void setSalePaySum(Double salePaySum) {
		this.salePaySum = salePaySum;
	}

	public Double getRefundPaySum() {
		return refundPaySum;
	}

	public void setRefundPaySum(Double refundPaySum) {
		this.refundPaySum = refundPaySum;
	}

}
