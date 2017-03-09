package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pc_merchant_setting")
public class MerchantSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private boolean sendSms;
	private boolean sendEmail;
	private int expiryDays;
	private String rzpMerchantId;
	private String rzpKeyId;
	private String rzpSecretId;

	@OneToOne(mappedBy = "setting")
	private Merchant merchant;

	public Integer getId() {
		return id;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
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

	public String getRzpMerchantId() {
		return rzpMerchantId;
	}

	public void setRzpMerchantId(String rzpMerchantId) {
		this.rzpMerchantId = rzpMerchantId;
	}

	public String getRzpKeyId() {
		return rzpKeyId;
	}

	public void setRzpKeyId(String rzpKeyId) {
		this.rzpKeyId = rzpKeyId;
	}

	public String getRzpSecretId() {
		return rzpSecretId;
	}

	public void setRzpSecretId(String rzpSecretId) {
		this.rzpSecretId = rzpSecretId;
	}

}
