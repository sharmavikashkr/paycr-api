package com.paycr.common.bean.gst;

import java.math.BigDecimal;

import com.paycr.common.type.SupplyType;

import lombok.Data;

@Data
public class Gstr2Nil {

	private SupplyType supplyType;
	private BigDecimal nilRated;
	private BigDecimal exempted;
	private BigDecimal nonGst;

}
