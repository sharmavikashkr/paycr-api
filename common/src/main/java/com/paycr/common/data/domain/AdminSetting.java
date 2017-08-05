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

	@Column(precision = 10, scale = 2)
	private Float tax;

	private String banner;

	@OneToOne(cascade = CascadeType.ALL)
	private PaymentSetting paymentSetting;

	public Integer getId() {
		return id;
	}

	public Float getTax() {
		return tax;
	}

	public void setTax(Float tax) {
		this.tax = tax;
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

}
