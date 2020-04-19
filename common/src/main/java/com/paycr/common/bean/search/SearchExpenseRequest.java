package com.paycr.common.bean.search;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

import com.paycr.common.type.ExpenseStatus;

@Data
public class SearchExpenseRequest {

	private Integer merchant;
	private String expenseCode;
	private String invoiceCode;
	private String email;
	private String mobile;
	private BigDecimal amount;
	private ExpenseStatus expenseStatus;
	private String itemCode;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdFrom;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdTo;

}
