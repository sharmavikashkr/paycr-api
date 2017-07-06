package com.paycr.common.data.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pc_merchant")
public class Merchant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String name;
	private String email;
	private String mobile;
	private String accessKey;
	private String secretKey;
	private boolean active;

	@Embedded
	private Address address;

	@OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
	private List<MerchantPricing> pricings;

	@OneToMany(mappedBy = "merchant")
	private List<InvoiceSetting> invoiceSettings;

	@OneToOne(cascade = CascadeType.ALL)
	private PaymentSetting paymentSetting;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<MerchantPricing> getPricings() {
		return pricings;
	}

	public void setPricings(List<MerchantPricing> pricings) {
		this.pricings = pricings;
	}

	public PaymentSetting getPaymentSetting() {
		return paymentSetting;
	}

	public void setPaymentSetting(PaymentSetting paymentSetting) {
		this.paymentSetting = paymentSetting;
	}

	public List<InvoiceSetting> getInvoiceSettings() {
		return invoiceSettings;
	}

	public void setInvoiceSettings(List<InvoiceSetting> invoiceSettings) {
		this.invoiceSettings = invoiceSettings;
	}
}
