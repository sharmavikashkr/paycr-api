package com.paycr.common.bean;

import java.util.List;

import com.paycr.common.data.domain.Merchant;

public class SearchMerchantResponse {

	private List<Merchant> merchantList;
	private int page;
	private int noOfPages;
	private List<Integer> allPages;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<Merchant> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchant> merchantList) {
		this.merchantList = merchantList;
	}

	public List<Integer> getAllPages() {
		return allPages;
	}

	public void setAllPages(List<Integer> allPages) {
		this.allPages = allPages;
	}

	public int getNoOfPages() {
		return noOfPages;
	}

	public void setNoOfPages(int noOfPages) {
		this.noOfPages = noOfPages;
	}

}
