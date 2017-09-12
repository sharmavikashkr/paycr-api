package com.paycr.common.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class StatsResponse {

	private BigDecimal salePaySum;
	private BigDecimal unpaidInvSum;
	private BigDecimal declinedInvSum;
	private BigDecimal expiredInvSum;
	private BigDecimal refundPaySum;
	private BigInteger salePayCount;
	private BigInteger unpaidInvCount;
	private BigInteger declinedInvCount;
	private BigInteger expiredInvCount;
	private BigInteger refundPayCount;
	private List<DailyPay> dailyPayList;

	public BigInteger getSalePayCount() {
		return salePayCount;
	}

	public void setSalePayCount(BigInteger salePayCount) {
		this.salePayCount = salePayCount;
	}

	public BigInteger getUnpaidInvCount() {
		return unpaidInvCount;
	}

	public void setUnpaidInvCount(BigInteger unpaidInvCount) {
		this.unpaidInvCount = unpaidInvCount;
	}

	public BigInteger getDeclinedInvCount() {
		return declinedInvCount;
	}

	public void setDeclinedInvCount(BigInteger declinedInvCount) {
		this.declinedInvCount = declinedInvCount;
	}

	public BigInteger getExpiredInvCount() {
		return expiredInvCount;
	}

	public void setExpiredInvCount(BigInteger expiredInvCount) {
		this.expiredInvCount = expiredInvCount;
	}

	public BigInteger getRefundPayCount() {
		return refundPayCount;
	}

	public void setRefundPayCount(BigInteger refundPayCount) {
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
