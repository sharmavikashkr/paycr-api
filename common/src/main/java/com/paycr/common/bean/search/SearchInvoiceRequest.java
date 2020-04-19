package com.paycr.common.bean.search;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;

@Data
public class SearchInvoiceRequest {

	private Integer merchant;
	private String invoiceCode;
	private InvoiceType invoiceType;
	private String parentInvoiceCode;
	private String email;
	private String mobile;
	private BigDecimal amount;
	private InvoiceStatus invoiceStatus;
	private String itemCode;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdFrom;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdTo;

}
