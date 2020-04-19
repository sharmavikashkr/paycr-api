package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import lombok.Data;

@Data
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

}
