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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.ItemType;

@Data
@Entity
@Table(name = "pc_inventory")
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;

	@NotEmpty
	private String name;

	@NotEmpty
	private String code;

	private String hsnsac;
	private String description;

	@NotNull
	private BigDecimal rate;
	private String createdBy;

	@Enumerated(EnumType.STRING)
	private ItemType type;

	private boolean active;

	@ManyToOne
	private TaxMaster tax;

	@JsonIgnore
	@ManyToOne
	private Merchant merchant;
}
