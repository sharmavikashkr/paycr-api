package com.paycr.common.data.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.paycr.common.type.PricingType;

@Entity
@Table(name = "pc_pricing")
public class Pricing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String code;
	private String name;
	private String description;
	private int invoiceLimit;
	private BigDecimal startAmount;
	private BigDecimal endAmount;
	private BigDecimal rate;
	private int duration;

	@Enumerated(EnumType.STRING)
	private PricingType type;

	private String hsnsac;

	@ManyToOne
	private TaxMaster tax;

	private boolean active;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getHsnsac() {
		return hsnsac;
	}

	public void setHsnsac(String hsnsac) {
		this.hsnsac = hsnsac;
	}

	public TaxMaster getTax() {
		return tax;
	}

	public void setTax(TaxMaster tax) {
		this.tax = tax;
	}

	public PricingType getType() {
		return type;
	}

	public void setType(PricingType type) {
		this.type = type;
	}

	public int getInvoiceLimit() {
		return invoiceLimit;
	}

	public void setInvoiceLimit(int invoiceLimit) {
		this.invoiceLimit = invoiceLimit;
	}

	public BigDecimal getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(BigDecimal startAmount) {
		this.startAmount = startAmount;
	}

	public BigDecimal getEndAmount() {
		return endAmount;
	}

	public void setEndAmount(BigDecimal endAmount) {
		this.endAmount = endAmount;
	}

}
