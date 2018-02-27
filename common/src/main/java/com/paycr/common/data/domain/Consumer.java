package com.paycr.common.data.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.ConsumerType;

@Entity
@Table(name = "pc_consumer")
public class Consumer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;

	@NotEmpty
	private String name;

	@NotEmpty
	private String email;

	@NotEmpty
	private String mobile;

	private String gstin;
	private boolean emailOnPay;
	private boolean emailOnRefund;
	private boolean emailNote;

	@Enumerated(EnumType.STRING)
	private ConsumerType type;

	private boolean active;
	private String createdBy;

	@OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL)
	private List<ConsumerFlag> flags;

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

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public ConsumerType getType() {
		return type;
	}

	public void setType(ConsumerType type) {
		this.type = type;
	}

	public List<ConsumerFlag> getFlags() {
		return flags;
	}

	public void setFlags(List<ConsumerFlag> flags) {
		this.flags = flags;
	}

	public boolean isEmailNote() {
		return emailNote;
	}

	public void setEmailNote(boolean emailNote) {
		this.emailNote = emailNote;
	}

}
