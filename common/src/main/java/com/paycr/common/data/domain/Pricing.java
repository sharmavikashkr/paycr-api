package com.paycr.common.data.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
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

	@Column(name = "use_limit")
	private int limit;
	private BigDecimal rate;
	private int duration;

	@Enumerated(EnumType.STRING)
	private PricingType type;

	private String hsnsac;

	@ManyToOne
	private TaxMaster interstateTax;

	@ManyToOne
	private TaxMaster intrastateTax;

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

	public PricingType getType() {
		return type;
	}

	public void setType(PricingType type) {
		this.type = type;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public TaxMaster getInterstateTax() {
		return interstateTax;
	}

	public void setInterstateTax(TaxMaster interstateTax) {
		this.interstateTax = interstateTax;
	}

	public TaxMaster getIntrastateTax() {
		return intrastateTax;
	}

	public void setIntrastateTax(TaxMaster intrastateTax) {
		this.intrastateTax = intrastateTax;
	}

}
