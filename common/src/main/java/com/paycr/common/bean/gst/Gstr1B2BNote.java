package com.paycr.common.bean.gst;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.type.NoteType;
import com.paycr.common.type.SupplyType;

import lombok.Data;

@Data
public class Gstr1B2BNote {

	private String gstin;
	private String noteNo;
	private Date noteDate;
	private String invoiceNo;
	private Date invoiceDate;
	private NoteType noteType;
	private BigDecimal taxableAmount;
	private BigDecimal noteAmount;
	private SupplyType supplyType;
	private String noteReason;
	private List<TaxAmount> taxAmount;

}
