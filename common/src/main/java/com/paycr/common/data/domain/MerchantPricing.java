package com.paycr.common.data.domain;

import java.util.Date;
import java.util.List;

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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.PricingStatus;

@Entity
@Table(name = "pc_merchant_pricing")
public class MerchantPricing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private Date startDate;
	private Date endDate;

	@Enumerated(EnumType.STRING)
	private PricingStatus status;

	@JsonIgnore
	@ManyToOne
	private Merchant merchant;

	@OneToOne
	private Pricing pricing;

	@JsonIgnore
	@OneToMany(mappedBy = "merchantPricing")
	private List<Invoice> invoices;

	@Transient
	private int invNo;

	public Integer getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Pricing getPricing() {
		return pricing;
	}

	public void setPricing(Pricing pricing) {
		this.pricing = pricing;
	}

	public PricingStatus getStatus() {
		return status;
	}

	public void setStatus(PricingStatus status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public int getInvNo() {
		return invNo;
	}

	public void setInvNo(int invNo) {
		this.invNo = invNo;
	}

}
