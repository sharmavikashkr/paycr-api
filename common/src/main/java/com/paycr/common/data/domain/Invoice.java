package com.paycr.common.data.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.Currency;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_invoice")
public class Invoice implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String invoiceCode;

	@ManyToOne
	private Merchant merchant;

	@Enumerated(EnumType.STRING)
	private InvoiceType invoiceType;

	@NotNull
	private Date invoiceDate;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal total;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal totalPrice;

	@Column(precision = 10, scale = 2)
	private BigDecimal shipping;

	@Column(precision = 10, scale = 2)
	private BigDecimal discount;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal payAmount;

	private boolean addItems;
	private Date expiry;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Currency currency;

	@ManyToOne(cascade = CascadeType.ALL)
	private Consumer consumer;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<InvoiceItem> items;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<InvoiceCustomParam> customParams;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<InvoiceNotify> notices;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<InvoiceAttachment> attachments;

	@ManyToOne
	private MerchantPricing merchantPricing;

	@OneToOne(cascade = CascadeType.ALL)
	private InvoicePayment payment;

	@OneToOne(cascade = CascadeType.ALL)
	private InvoiceNote note;

	@Enumerated(EnumType.STRING)
	private InvoiceStatus status;

	private String createdBy;
	private int expiresIn;
	private Date updated;
	private String updatedBy;
	private boolean deleted;

	@JsonIgnore
	@ManyToOne
	private Invoice parent;

	@Transient
	private boolean update;
}
