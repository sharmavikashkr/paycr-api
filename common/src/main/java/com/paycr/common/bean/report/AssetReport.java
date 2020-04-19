package com.paycr.common.bean.report;

import java.math.BigInteger;

import lombok.Data;

@Data
public class AssetReport {

	private String code;
	private String name;
	private Double rate;
	private BigInteger saleQuantity;
	private Double saleAmt;

}
