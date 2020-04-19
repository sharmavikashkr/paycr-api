package com.paycr.common.bean.gst;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.type.SupplyType;

import lombok.Data;

@Data
public class Gstr2B2BR {

	private String gstin;
	private String invoiceNo;
	private BigDecimal taxableAmount;
	private BigDecimal invoiceAmount;
	private Date invoiceDate;
	private String placeOfSupply;
	private SupplyType supplyType;
	private List<TaxAmount> taxAmount;

}
