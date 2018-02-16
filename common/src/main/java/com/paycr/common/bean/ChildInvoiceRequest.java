package com.paycr.common.bean;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerFlag;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.type.InvoiceType;

public class ChildInvoiceRequest {

	private Consumer consumer;
	private List<ConsumerFlag> flagList;

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

	public List<ConsumerFlag> getFlagList() {
		return flagList;
	}

	public void setFlagList(List<ConsumerFlag> flagList) {
		this.flagList = flagList;
	}
}
