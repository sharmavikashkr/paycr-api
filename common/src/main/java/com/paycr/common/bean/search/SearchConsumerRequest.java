package com.paycr.common.bean.search;

import java.util.List;

import com.paycr.common.data.domain.ConsumerFlag;

public class SearchConsumerRequest {

	private Integer merchant;
	private String name;
	private String email;
	private String mobile;
	private List<ConsumerFlag> flagList;

	public Integer getMerchant() {
		return merchant;
	}

	public void setMerchant(Integer merchant) {
		this.merchant = merchant;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ConsumerFlag> getFlagList() {
		return flagList;
	}

	public void setFlagList(List<ConsumerFlag> flagList) {
		this.flagList = flagList;
	}
}
