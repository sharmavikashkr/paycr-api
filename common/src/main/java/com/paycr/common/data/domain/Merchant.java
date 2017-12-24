package com.paycr.common.data.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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
	private String gstin;
	private boolean active;
	private String banner;

	@Transient
	private boolean enableWelcome;

	@OneToOne(cascade = CascadeType.ALL)
	private Address address;

	@OneToMany(mappedBy = "merchant")
	private List<MerchantPricing> pricings;

	@OneToOne(cascade = CascadeType.ALL)
	private InvoiceSetting invoiceSetting;

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

	public InvoiceSetting getInvoiceSetting() {
		return invoiceSetting;
	}

	public void setInvoiceSetting(InvoiceSetting invoiceSetting) {
		this.invoiceSetting = invoiceSetting;
	}

	public boolean isEnableWelcome() {
		return enableWelcome;
	}

	public void setEnableWelcome(boolean enableWelcome) {
		this.enableWelcome = enableWelcome;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}
}
