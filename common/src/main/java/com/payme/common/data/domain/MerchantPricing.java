package com.payme.common.data.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.payme.common.type.PricingStatus;

@Entity
@Table(name = "pm_merchant_pricing")
public class MerchantPricing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private Date startDate;
	private Date endDate;
	private int noOfInvoice;

	@Enumerated(EnumType.STRING)
	private PricingStatus status;

	@ManyToOne
	private Merchant merchant;

	@OneToOne
	private Pricing pricing;

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

	public int getNoOfInvoice() {
		return noOfInvoice;
	}

	public void setNoOfInvoice(int noOfInvoice) {
		this.noOfInvoice = noOfInvoice;
	}

}
