package com.paycr.common.bean;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.paycr.common.type.PayMode;

public class SearchSubsRequest {

	private Integer merchant;
	private Integer pricing;
	private PayMode payMode;
	private String subsCode;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdFrom;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdTo;

	public Integer getMerchant() {
		return merchant;
	}

	public void setMerchant(Integer merchant) {
		this.merchant = merchant;
	}

	public Integer getPricing() {
		return pricing;
	}

	public void setPricing(Integer pricing) {
		this.pricing = pricing;
	}

	public String getSubsCode() {
		return subsCode;
	}

	public void setSubsCode(String subsCode) {
		this.subsCode = subsCode;
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

	public PayMode getPayMode() {
		return payMode;
	}

	public void setPayMode(PayMode payMode) {
		this.payMode = payMode;
	}

}
