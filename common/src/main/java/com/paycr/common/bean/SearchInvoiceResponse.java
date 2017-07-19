package com.paycr.common.bean;

import java.util.List;

import com.paycr.common.data.domain.Invoice;

public class SearchInvoiceResponse {

	private List<Invoice> invoiceList;
	private int page;
	private List<Integer> allPages;

	public List<Invoice> getInvoiceList() {
		return invoiceList;
	}

	public void setInvoiceList(List<Invoice> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<Integer> getAllPages() {
		return allPages;
	}

	public void setAllPages(List<Integer> allPages) {
		this.allPages = allPages;
	}

}
