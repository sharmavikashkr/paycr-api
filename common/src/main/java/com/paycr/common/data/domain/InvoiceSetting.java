package com.paycr.common.data.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pc_invoice_setting")
public class InvoiceSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;
	private boolean sendSms;
	private boolean sendEmail;
	private int expiryDays;

	@JsonIgnore
	@ManyToOne
	private Merchant merchant;

	@OneToMany(mappedBy = "invoiceSetting", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MerchantCustomParam> customParams;

	public Integer getId() {
		return id;
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

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
