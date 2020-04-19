package com.paycr.common.bean.search;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;

@Data
public class SearchInvoicePaymentRequest {

	private Integer merchant;
	private String invoiceCode;
	private String paymentRefNo;
	private PayType payType;
	private PayMode payMode;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdFrom;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdTo;

}
