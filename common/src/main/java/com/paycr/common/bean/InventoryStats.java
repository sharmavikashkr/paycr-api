package com.paycr.common.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InventoryStats {

	private BigDecimal paidSum;
	private BigDecimal unpaidSum;
	private BigDecimal declinedSum;
	private BigDecimal expiredSum;
	private BigDecimal createdSum;
	private Long paidNo;
	private Long unpaidNo;
	private Long declinedNo;
	private Long expiredNo;
	private Long createdNo;

}
