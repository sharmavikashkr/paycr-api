package com.paycr.common.bean;

import java.math.BigDecimal;
import java.util.List;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Payment;

public class StatsResponse {

	private BigDecimal paidInvSum;
	private BigDecimal unpaidInvSum;
	private BigDecimal declinedInvSum;
	private BigDecimal expiredInvSum;
	private BigDecimal refundPaySum;
	private List<Invoice> paidInvs;
	private List<Invoice> unpaidInvs;
	private List<Invoice> declinedInvs;
	private List<Invoice> expiredInvs;
	private List<Payment> refundPays;

	public List<Invoice> getPaidInvs() {
		return paidInvs;
	}

	public void setPaidInvs(List<Invoice> paidInvs) {
		this.paidInvs = paidInvs;
	}

	public List<Invoice> getUnpaidInvs() {
		return unpaidInvs;
	}

	public void setUnpaidInvs(List<Invoice> unpaidInvs) {
		this.unpaidInvs = unpaidInvs;
	}

	public List<Invoice> getDeclinedInvs() {
		return declinedInvs;
	}

	public void setDeclinedInvs(List<Invoice> declinedInvs) {
		this.declinedInvs = declinedInvs;
	}

	public List<Invoice> getExpiredInvs() {
		return expiredInvs;
	}

	public void setExpiredInvs(List<Invoice> expiredInvs) {
		this.expiredInvs = expiredInvs;
	}

	public List<Payment> getRefundPays() {
		return refundPays;
	}

	public void setRefundPays(List<Payment> refundPays) {
		this.refundPays = refundPays;
	}

	public BigDecimal getPaidInvSum() {
		return paidInvSum;
	}

	public void setPaidInvSum(BigDecimal paidInvSum) {
		this.paidInvSum = paidInvSum;
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

}
