package com.paycr.common.bean;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerCategory;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.type.InvoiceType;

public class ChildInvoiceRequest {

	private Consumer consumer;
	private List<ConsumerCategory> conCatList;

	@NotNull
	private InvoiceType invoiceType;
	private RecurringInvoice recInv;

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}

	public RecurringInvoice getRecInv() {
		return recInv;
	}

	public void setRecInv(RecurringInvoice recInv) {
		this.recInv = recInv;
	}

	public List<ConsumerCategory> getConCatList() {
		return conCatList;
	}

	public void setConCatList(List<ConsumerCategory> conCatList) {
		this.conCatList = conCatList;
	}

}
