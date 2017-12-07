package com.paycr.common.data.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pc_consumer")
public class Consumer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String name;
	private String email;
	private String mobile;
	private String gstin;
	private boolean emailOnPay;
	private boolean emailOnRefund;

	private boolean active;
	private String createdBy;

	@OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL)
	private List<ConsumerCategory> conCats;

	@JsonIgnore
	@ManyToOne
	private Merchant merchant;

	@OneToOne(cascade = CascadeType.ALL)
	private Address billingAddress;

	@OneToOne(cascade = CascadeType.ALL)
	private Address shippingAddress;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Integer getId() {
		return id;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public boolean isEmailOnPay() {
		return emailOnPay;
	}

	public void setEmailOnPay(boolean emailOnPay) {
		this.emailOnPay = emailOnPay;
	}

	public boolean isEmailOnRefund() {
		return emailOnRefund;
	}

	public void setEmailOnRefund(boolean emailOnRefund) {
		this.emailOnRefund = emailOnRefund;
	}

	public List<ConsumerCategory> getConCats() {
		return conCats;
	}

	public void setConCats(List<ConsumerCategory> conCats) {
		this.conCats = conCats;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

}
