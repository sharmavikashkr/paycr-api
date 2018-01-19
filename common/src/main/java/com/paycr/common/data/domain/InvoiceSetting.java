package com.paycr.common.data.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "pc_invoice_setting")
public class InvoiceSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private boolean sendSms;
	private boolean sendEmail;
	private boolean addItems;
	private boolean emailPdf;
	private boolean refundCreditNote;
	private boolean ccMe;
	private int expiryDays;
	private String emailNote;
	private String emailSubject;

	@OneToMany(mappedBy = "invoiceSetting", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MerchantCustomParam> customParams;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isSendSms() {
		return sendSms;
	}

	public void setSendSms(boolean sendSms) {
		this.sendSms = sendSms;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public int getExpiryDays() {
		return expiryDays;
	}

	public void setExpiryDays(int expiryDays) {
		this.expiryDays = expiryDays;
	}

	public List<MerchantCustomParam> getCustomParams() {
		return customParams;
	}

	public void setCustomParams(List<MerchantCustomParam> customParams) {
		this.customParams = customParams;
	}

	public boolean isAddItems() {
		return addItems;
	}

	public void setAddItems(boolean addItems) {
		this.addItems = addItems;
	}

	public boolean isEmailPdf() {
		return emailPdf;
	}

	public void setEmailPdf(boolean emailPdf) {
		this.emailPdf = emailPdf;
	}

	public boolean isCcMe() {
		return ccMe;
	}

	public void setCcMe(boolean ccMe) {
		this.ccMe = ccMe;
	}

	public String getEmailNote() {
		return emailNote;
	}

	public void setEmailNote(String emailNote) {
		this.emailNote = emailNote;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public boolean isRefundCreditNote() {
		return refundCreditNote;
	}

	public void setRefundCreditNote(boolean refundCreditNote) {
		this.refundCreditNote = refundCreditNote;
	}

}
