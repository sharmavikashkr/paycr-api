package com.paycr.common.bean;

import java.math.BigDecimal;
import java.util.List;

public class StatsResponse {

	private BigDecimal salePaySum;
	private BigDecimal unpaidInvSum;
	private BigDecimal declinedInvSum;
	private BigDecimal expiredInvSum;
	private BigDecimal refundPaySum;
	private int salePayCount;
	private int unpaidInvCount;
	private int declinedInvCount;
	private int expiredInvCount;
	private int refundPayCount;
	private List<DailyPay> dailyPayList;

	public int getSalePayCount() {
		return salePayCount;
	}

	public void setSalePayCount(int salePayCount) {
		this.salePayCount = salePayCount;
	}

	public int getUnpaidInvCount() {
		return unpaidInvCount;
	}

	public void setUnpaidInvCount(int unpaidInvCount) {
		this.unpaidInvCount = unpaidInvCount;
	}

	public int getDeclinedInvCount() {
		return declinedInvCount;
	}

	public void setDeclinedInvCount(int declinedInvCount) {
		this.declinedInvCount = declinedInvCount;
	}

	public int getExpiredInvCount() {
		return expiredInvCount;
	}

	public void setExpiredInvCount(int expiredInvCount) {
		this.expiredInvCount = expiredInvCount;
	}

	public int getRefundPayCount() {
		return refundPayCount;
	}

	public void setRefundPayCount(int refundPayCount) {
		this.refundPayCount = refundPayCount;
	}

	public BigDecimal getUnpaidInvSum() {
		return unpaidInvSum;
	}

	public void setUnpaidInvSum(BigDecimal unpaidInvSum) {
		this.unpaidInvSum = unpaidInvSum;
	}

	public BigDecimal getDeclinedInvSum() {
		return declinedInvSum;
	}

	public void setDeclinedInvSum(BigDecimal declinedInvSum) {
		this.declinedInvSum = declinedInvSum;
	}

	public BigDecimal getExpiredInvSum() {
		return expiredInvSum;
	}

	public void setExpiredInvSum(BigDecimal expiredInvSum) {
		this.expiredInvSum = expiredInvSum;
	}

	public BigDecimal getRefundPaySum() {
		return refundPaySum;
	}

	public void setRefundPaySum(BigDecimal refundPaySum) {
		this.refundPaySum = refundPaySum;
	}

	public BigDecimal getSalePaySum() {
		return salePaySum;
	}

	public void setSalePaySum(BigDecimal salePaySum) {
		this.salePaySum = salePaySum;
	}

	public List<DailyPay> getDailyPayList() {
		return dailyPayList;
	}

	public void setDailyPayList(List<DailyPay> dailyPayList) {
		this.dailyPayList = dailyPayList;
	}

}
