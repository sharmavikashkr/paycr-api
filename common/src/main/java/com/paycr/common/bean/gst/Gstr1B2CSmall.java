package com.paycr.common.bean.gst;

import java.math.BigDecimal;

import com.paycr.common.type.SupplyType;

import lombok.Data;

@Data
public class Gstr1B2CSmall {

	private SupplyType supplyType;
	private float gstRate;
	private BigDecimal taxableAmount;
	private BigDecimal cgstAmount;
	private BigDecimal sgstAmount;
	private BigDecimal igstAmount;

}
