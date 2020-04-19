package com.paycr.common.bean.report;

import java.math.BigInteger;

import lombok.Data;

@Data
public class SupplierReport {

	private String name;
	private String email;
	private String mobile;
	private BigInteger expenses;
	private BigInteger refunded;
	private Double expenseAmt;
	private Double refundAmt;

}
