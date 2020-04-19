package com.paycr.common.bean.report;

import java.math.BigInteger;

import lombok.Data;

@Data
public class ConsumerReport {

	private String name;
	private String email;
	private String mobile;
	private BigInteger invoices;
	private BigInteger refunded;
	private Double invoiceAmt;
	private Double refundAmt;

}
