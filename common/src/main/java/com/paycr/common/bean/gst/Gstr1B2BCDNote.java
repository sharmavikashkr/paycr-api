package com.paycr.common.bean.gst;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.paycr.common.bean.TaxAmount;

public class Gstr1B2BCDNote {

	private String gstin;
	private String noteNo;
	private Date noteDate;
	private float invoiceNo;
	private Date invoiceDate;
	private String noteType;
	private BigDecimal noteAmount;
	private String supplyType;
	private String noteReason;
	private List<TaxAmount> taxAmount;

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getNoteNo() {
		return noteNo;
	}

	public void setNoteNo(String noteNo) {
		this.noteNo = noteNo;
	}

	public Date getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}

	public float getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(float invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}

	public BigDecimal getNoteAmount() {
		return noteAmount;
	}

	public void setNoteAmount(BigDecimal noteAmount) {
		this.noteAmount = noteAmount;
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

	public String getNoteReason() {
		return noteReason;
	}

	public void setNoteReason(String noteReason) {
		this.noteReason = noteReason;
	}

}
