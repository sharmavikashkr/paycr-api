package com.paycr.common.data.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.paycr.common.type.PayMode;

@Entity
@Table(name = "pc_subscription_mode")
public class SubscriptionMode implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;

	@Enumerated(EnumType.STRING)
	private PayMode payMode;

	private String rzpMerchantId;
	private String rzpKeyId;
	private String rzpSecretId;

	private boolean active;

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

	public Integer getId() {
		return id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PayMode getPayMode() {
		return payMode;
	}

	public void setPayMode(PayMode payMode) {
		this.payMode = payMode;
	}

}
