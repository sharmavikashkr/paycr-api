package com.paycr.common.bean;

import java.math.BigInteger;

public class ConsumerReport {

	private String name;
	private String email;
	private String mobile;
	private BigInteger invoices;
	private BigInteger refunded;
	private Double invoiceAmt;
	private Double refundAmt;

	public BigInteger getInvoices() {
		return invoices;
	}

	public void setInvoices(BigInteger invoices) {
		this.invoices = invoices;
	}

	public BigInteger getRefunded() {
		return refunded;
	}

	public void setRefunded(BigInteger refunded) {
		this.refunded = refunded;
	}

	public Double getInvoiceAmt() {
		return invoiceAmt;
	}

	public void setInvoiceAmt(Double invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}

	public Double getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(Double refundAmt) {
		this.refundAmt = refundAmt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
