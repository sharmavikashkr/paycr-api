package com.paycr.common.bean.report;

import java.math.BigDecimal;
import java.util.Date;

import com.paycr.common.type.Currency;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;

import lombok.Data;

@Data
public class ExpenseReport {

	private Date paidDate;
	private String expenseCode;
	private ExpenseStatus expenseStatus;
	private BigDecimal payAmount;
	private BigDecimal tax;
	private BigDecimal shipping;
	private BigDecimal discount;
	private BigDecimal amount;
	private Currency currency;
	private String paymentRefNo;
	private PayType payType;
	private PayMode payMode;
	private String payMethod;
	private String payStatus;

}
