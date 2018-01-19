package com.paycr.common.bean.search;

import java.util.List;

import com.paycr.common.data.domain.ConsumerCategory;

public class SearchConsumerRequest {

	private Integer merchant;
	private String email;
	private String mobile;
	private List<ConsumerCategory> conCatList;

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

	public List<ConsumerCategory> getConCatList() {
		return conCatList;
	}

	public void setConCatList(List<ConsumerCategory> conCatList) {
		this.conCatList = conCatList;
	}
}
