package com.paycr.common.bean.report;

import java.math.BigInteger;

public class SupplierReport {

	private String name;
	private String email;
	private String mobile;
	private BigInteger expenses;
	private BigInteger refunded;
	private Double expenseAmt;
	private Double refundAmt;

	public BigInteger getExpenses() {
		return expenses;
	}

	public void setExpenses(BigInteger expenses) {
		this.expenses = expenses;
	}

	public BigInteger getRefunded() {
		return refunded;
	}

	public void setRefunded(BigInteger refunded) {
		this.refunded = refunded;
	}

	public Double getExpenseAmt() {
		return expenseAmt;
	}

	public void setExpenseAmt(Double expenseAmt) {
		this.expenseAmt = expenseAmt;
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
