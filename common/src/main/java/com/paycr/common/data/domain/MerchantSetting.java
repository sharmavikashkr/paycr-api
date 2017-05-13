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

	@OneToMany(mappedBy = "merchantSetting", cascade = CascadeType.ALL, orphanRemoval = true)
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

	public List<MerchantCustomParam> getCustomParams() {
		return customParams;
	}

	public void setCustomParams(List<MerchantCustomParam> customParams) {
		this.customParams = customParams;
	}

}
