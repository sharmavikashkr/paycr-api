package com.paycr.common.data.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pc_invoice_notify")
public class InvoiceNotify implements Serializable {

	private static final long serialVersionUID = -8798244987005274799L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;

	@ManyToOne
	@JsonIgnore
	private Invoice invoice;

	private boolean sendEmail;
	private boolean sendSms;
	private boolean emailPdf;
	private boolean ccMe;
	private String ccEmail;
	private String emailNote;
	private String emailSubject;

	public Integer getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public boolean isSendSms() {
		return sendSms;
	}

	public void setSendSms(boolean sendSms) {
		this.sendSms = sendSms;
	}

	public boolean isEmailPdf() {
		return emailPdf;
	}

	public void setEmailPdf(boolean emailPdf) {
		this.emailPdf = emailPdf;
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

	public boolean isCcMe() {
		return ccMe;
	}

	public void setCcMe(boolean ccMe) {
		this.ccMe = ccMe;
	}

	public String getCcEmail() {
		return ccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}

}
