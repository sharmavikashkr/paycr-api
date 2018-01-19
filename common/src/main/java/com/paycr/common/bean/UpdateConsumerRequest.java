package com.paycr.common.bean;

import java.util.List;

import com.paycr.common.bean.search.SearchConsumerRequest;
import com.paycr.common.data.domain.ConsumerCategory;

public class UpdateConsumerRequest {

	private boolean emailOnPay;
	private boolean emailOnRefund;
	private boolean active;
	private boolean removeOldTags;
	private List<ConsumerCategory> conCatList;
	private SearchConsumerRequest searchReq;

	public boolean isEmailOnPay() {
		return emailOnPay;
	}

	public void setEmailOnPay(boolean emailOnPay) {
		this.emailOnPay = emailOnPay;
	}

	public boolean isEmailOnRefund() {
		return emailOnRefund;
	}

	public void setEmailOnRefund(boolean emailOnRefund) {
		this.emailOnRefund = emailOnRefund;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isRemoveOldTags() {
		return removeOldTags;
	}

	public void setRemoveOldTags(boolean removeOldTags) {
		this.removeOldTags = removeOldTags;
	}

	public SearchConsumerRequest getSearchReq() {
		return searchReq;
	}

	public void setSearchReq(SearchConsumerRequest searchReq) {
		this.searchReq = searchReq;
	}

	public List<ConsumerCategory> getConCatList() {
		return conCatList;
	}

	public void setConCatList(List<ConsumerCategory> conCatList) {
		this.conCatList = conCatList;
	}
}
