package com.paycr.common.bean.gst;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.type.NoteType;
import com.paycr.common.type.SupplyType;

public class Gstr1B2CNote {

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

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public NoteType getNoteType() {
		return noteType;
	}

	public void setNoteType(NoteType noteType) {
		this.noteType = noteType;
	}

	public BigDecimal getNoteAmount() {
		return noteAmount;
	}

	public void setNoteAmount(BigDecimal noteAmount) {
		this.noteAmount = noteAmount;
	}

	public SupplyType getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(SupplyType supplyType) {
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

	public BigDecimal getTaxableAmount() {
		return taxableAmount;
	}

	public void setTaxableAmount(BigDecimal taxableAmount) {
		this.taxableAmount = taxableAmount;
	}

}
