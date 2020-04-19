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

import lombok.Data;

@Data
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

}
