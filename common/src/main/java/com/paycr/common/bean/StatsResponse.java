package com.paycr.common.bean;

import java.math.BigDecimal;
import java.util.List;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Payment;

public class StatsResponse {

	private BigDecimal salePaySum;
	private BigDecimal unpaidInvSum;
	private BigDecimal declinedInvSum;
	private BigDecimal expiredInvSum;
	private BigDecimal refundPaySum;
	private List<Payment> salePays;
	private List<Invoice> unpaidInvs;
	private List<Invoice> declinedInvs;
	private List<Invoice> expiredInvs;
	private List<Payment> refundPays;

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

	public List<Payment> getSalePays() {
		return salePays;
	}

	public void setSalePays(List<Payment> salePays) {
		this.salePays = salePays;
	}

	public BigDecimal getSalePaySum() {
		return salePaySum;
	}

	public void setSalePaySum(BigDecimal salePaySum) {
		this.salePaySum = salePaySum;
	}

}
