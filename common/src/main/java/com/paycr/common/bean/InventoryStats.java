package com.paycr.common.bean;

import java.math.BigDecimal;

public class InventoryStats {

	private BigDecimal paidSum;
	private BigDecimal unpaidSum;
	private BigDecimal declinedSum;
	private BigDecimal expiredSum;
	private BigDecimal createdSum;
	private Long paidNo;
	private Long unpaidNo;
	private Long declinedNo;
	private Long expiredNo;
	private Long createdNo;

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

	public BigDecimal getDeclinedSum() {
		return declinedSum;
	}

	public void setDeclinedSum(BigDecimal declinedSum) {
		this.declinedSum = declinedSum;
	}

	public BigDecimal getExpiredSum() {
		return expiredSum;
	}

	public void setExpiredSum(BigDecimal expiredSum) {
		this.expiredSum = expiredSum;
	}

	public BigDecimal getCreatedSum() {
		return createdSum;
	}

	public void setCreatedSum(BigDecimal createdSum) {
		this.createdSum = createdSum;
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

	public Long getDeclinedNo() {
		return declinedNo;
	}

	public void setDeclinedNo(Long declinedNo) {
		this.declinedNo = declinedNo;
	}

	public Long getExpiredNo() {
		return expiredNo;
	}

	public void setExpiredNo(Long expiredNo) {
		this.expiredNo = expiredNo;
	}

	public Long getCreatedNo() {
		return createdNo;
	}

	public void setCreatedNo(Long createdNo) {
		this.createdNo = createdNo;
	}

}
