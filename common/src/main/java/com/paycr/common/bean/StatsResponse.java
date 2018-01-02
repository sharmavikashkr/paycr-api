package com.paycr.common.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class StatsResponse {

	private BigDecimal saleInvPaySum;
	private BigDecimal unpaidInvSum;
	private BigDecimal declinedInvSum;
	private BigDecimal expiredInvSum;
	private BigDecimal refundInvPaySum;
	private BigInteger saleInvPayCount;
	private BigInteger unpaidInvCount;
	private BigInteger declinedInvCount;
	private BigInteger expiredInvCount;
	private BigInteger refundInvPayCount;
	private List<DailyPay> dailyInvPayList;

	private BigDecimal saleExpPaySum;
	private BigDecimal unpaidExpSum;
	private BigDecimal refundExpPaySum;
	private BigInteger saleExpPayCount;
	private BigInteger unpaidExpCount;
	private BigInteger refundExpPayCount;

	public BigDecimal getSaleExpPaySum() {
		return saleExpPaySum;
	}

	public void setSaleExpPaySum(BigDecimal saleExpPaySum) {
		this.saleExpPaySum = saleExpPaySum;
	}

	public BigDecimal getUnpaidExpSum() {
		return unpaidExpSum;
	}

	public void setUnpaidExpSum(BigDecimal unpaidExpSum) {
		this.unpaidExpSum = unpaidExpSum;
	}

	public BigDecimal getRefundExpPaySum() {
		return refundExpPaySum;
	}

	public void setRefundExpPaySum(BigDecimal refundExpPaySum) {
		this.refundExpPaySum = refundExpPaySum;
	}

	public BigInteger getSaleExpPayCount() {
		return saleExpPayCount;
	}

	public void setSaleExpPayCount(BigInteger saleExpPayCount) {
		this.saleExpPayCount = saleExpPayCount;
	}

	public BigInteger getUnpaidExpCount() {
		return unpaidExpCount;
	}

	public void setUnpaidExpCount(BigInteger unpaidExpCount) {
		this.unpaidExpCount = unpaidExpCount;
	}

	public BigInteger getRefundExpPayCount() {
		return refundExpPayCount;
	}

	public void setRefundExpPayCount(BigInteger refundExpPayCount) {
		this.refundExpPayCount = refundExpPayCount;
	}

	public List<DailyPay> getDailyExpPayList() {
		return dailyExpPayList;
	}

	public void setDailyExpPayList(List<DailyPay> dailyExpPayList) {
		this.dailyExpPayList = dailyExpPayList;
	}

	private List<DailyPay> dailyExpPayList;

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

	public BigDecimal getSaleInvPaySum() {
		return saleInvPaySum;
	}

	public void setSaleInvPaySum(BigDecimal saleInvPaySum) {
		this.saleInvPaySum = saleInvPaySum;
	}

	public BigDecimal getRefundInvPaySum() {
		return refundInvPaySum;
	}

	public void setRefundInvPaySum(BigDecimal refundInvPaySum) {
		this.refundInvPaySum = refundInvPaySum;
	}

	public BigInteger getSaleInvPayCount() {
		return saleInvPayCount;
	}

	public void setSaleInvPayCount(BigInteger saleInvPayCount) {
		this.saleInvPayCount = saleInvPayCount;
	}

	public BigInteger getRefundInvPayCount() {
		return refundInvPayCount;
	}

	public void setRefundInvPayCount(BigInteger refundInvPayCount) {
		this.refundInvPayCount = refundInvPayCount;
	}

	public List<DailyPay> getDailyInvPayList() {
		return dailyInvPayList;
	}

	public void setDailyInvPayList(List<DailyPay> dailyInvPayList) {
		this.dailyInvPayList = dailyInvPayList;
	}

}
