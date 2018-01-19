package com.paycr.common.bean.gst;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.paycr.common.bean.TaxAmount;

public class Gstr1B2CLarge {

	private float invoiceNo;
	private BigDecimal invoiceAmount;
	private Date invoiceDate;
	private String placeOfSupply;
	private String supplyType;
	private List<TaxAmount> taxAmount;

	public float getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(float invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getPlaceOfSupply() {
		return placeOfSupply;
	}

	public void setPlaceOfSupply(String placeOfSupply) {
		this.placeOfSupply = placeOfSupply;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public List<TaxAmount> getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(List<TaxAmount> taxAmount) {
		this.taxAmount = taxAmount;
	}

}
