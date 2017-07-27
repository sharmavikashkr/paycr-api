package com.paycr.common.bean;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.paycr.common.type.InvoiceStatus;

public class SearchInvoiceRequest {

	private Integer merchant;
	private String invoiceCode;
	private String email;
	private String mobile;
	private BigDecimal amount;
	private InvoiceStatus invoiceStatus;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdFrom;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdTo;

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getMerchant() {
		return merchant;
	}

	public void setMerchant(Integer merchant) {
		this.merchant = merchant;
	}

	public Date getCreatedFrom() {
		return createdFrom;
	}

	public void setCreatedFrom(Date createdFrom) {
		this.createdFrom = createdFrom;
	}

	public Date getCreatedTo() {
		return createdTo;
	}

	public void setCreatedTo(Date createdTo) {
		this.createdTo = createdTo;
	}

	public InvoiceStatus getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

}
