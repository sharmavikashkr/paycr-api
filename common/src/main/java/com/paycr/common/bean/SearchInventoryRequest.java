package com.paycr.common.bean;

public class SearchInventoryRequest {

	private Integer merchant;
	private String code;
	private String name;

	public Integer getMerchant() {
		return merchant;
	}

	public void setMerchant(Integer merchant) {
		this.merchant = merchant;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
