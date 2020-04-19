package com.paycr.common.bean;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerFlag;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.type.InvoiceType;

import lombok.Data;

@Data
public class ChildInvoiceRequest {

	private Consumer consumer;
	private List<ConsumerFlag> flagList;

	@NotNull
	private InvoiceType invoiceType;
	private RecurringInvoice recInv;

}
