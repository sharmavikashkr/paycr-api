package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pc_payment_setting")
public class PaymentSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String rzpMerchantId;
	private String rzpKeyId;
	private String rzpSecretId;

	public Integer getId() {
		return id;
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
