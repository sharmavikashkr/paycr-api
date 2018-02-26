package com.paycr.common.bean.report;

import java.math.BigDecimal;
import java.util.Date;

import com.paycr.common.type.Currency;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;

public class ExpenseReport {

	private Date paidDate;
	private String expenseCode;
	private ExpenseStatus expenseStatus;
	private BigDecimal payAmount;
	private BigDecimal tax;
	private BigDecimal shipping;
	private BigDecimal discount;
	private BigDecimal amount;
	private Currency currency;
	private String paymentRefNo;
	private PayType payType;
	private PayMode payMode;
	private String payMethod;
	private String payStatus;

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getPaymentRefNo() {
		return paymentRefNo;
	}

	public void setPaymentRefNo(String paymentRefNo) {
		this.paymentRefNo = paymentRefNo;
	}

	public PayType getPayType() {
		return payType;
	}

	public void setPayType(PayType payType) {
		this.payType = payType;
	}

	public PayMode getPayMode() {
		return payMode;
	}

	public void setPayMode(PayMode payMode) {
		this.payMode = payMode;
	}

	public String getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getExpenseCode() {
		return expenseCode;
	}

	public void setExpenseCode(String expenseCode) {
		this.expenseCode = expenseCode;
	}

	public ExpenseStatus getExpenseStatus() {
		return expenseStatus;
	}

	public void setExpenseStatus(ExpenseStatus expenseStatus) {
		this.expenseStatus = expenseStatus;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public BigDecimal getShipping() {
		return shipping;
	}

	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}
}
