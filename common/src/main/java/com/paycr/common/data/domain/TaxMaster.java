package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

@Entity
@Table(name = "pc_tax_master")
public class TaxMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;
	private Float value;

	private boolean active;
	private boolean child;

	@ManyToOne
	private TaxMaster parent;

	@Transient
	private Integer parentTaxId;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public TaxMaster getParent() {
		return parent;
	}

	public void setTaxParent(TaxMaster parent) {
		this.parent = parent;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isChild() {
		return child;
	}

	public void setChild(boolean child) {
		this.child = child;
	}

	public Integer getParentTaxId() {
		return parentTaxId;
	}

	public void setParentTaxId(Integer parentTaxId) {
		this.parentTaxId = parentTaxId;
	}

}
