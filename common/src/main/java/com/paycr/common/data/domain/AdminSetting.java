package com.paycr.common.data.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pc_admin_setting")
public class AdminSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String taxName;

	@Column(precision = 10, scale = 2)
	private Float taxValue;

	private String banner;

	@OneToOne(cascade = CascadeType.ALL)
	private PaymentSetting paymentSetting;

	public Integer getId() {
		return id;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public PaymentSetting getPaymentSetting() {
		return paymentSetting;
	}

	public void setPaymentSetting(PaymentSetting paymentSetting) {
		this.paymentSetting = paymentSetting;
	}

	public String getTaxName() {
		return taxName;
	}

	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	public Float getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(Float taxValue) {
		this.taxValue = taxValue;
	}

}
