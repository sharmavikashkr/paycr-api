package com.paycr.common.data.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_invoice_item")
public class InvoiceItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private int quantity;

	@NotNull
	private BigDecimal price;

	@ManyToOne
	private TaxMaster tax;

	@JsonIgnore
	@ManyToOne
	private Invoice invoice;

	@JsonIgnore
	@ManyToOne
	private InvoiceNote invoiceNote;

	@ManyToOne
	private Inventory inventory;

}
