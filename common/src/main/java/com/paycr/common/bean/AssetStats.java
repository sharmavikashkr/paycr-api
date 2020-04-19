package com.paycr.common.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AssetStats {

	private BigDecimal paidSum;
	private BigDecimal unpaidSum;
	private Long paidNo;
	private Long unpaidNo;

}
