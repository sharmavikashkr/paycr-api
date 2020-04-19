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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.paycr.common.type.Currency;
import com.paycr.common.type.ExpenseStatus;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_expense")
public class Expense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String expenseCode;

	@NotNull
	private String invoiceCode;

	@NotNull
	private Date invoiceDate;

	@ManyToOne
	private Merchant merchant;

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

	@NotNull
	@Enumerated(EnumType.STRING)
	private Currency currency;

	@Valid
	@NotNull
	@ManyToOne(cascade = CascadeType.ALL)
	private Supplier supplier;

	@OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
	private List<ExpenseItem> items;

	@OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
	private List<ExpenseAttachment> attachments;

	@ManyToOne
	private MerchantPricing merchantPricing;

	@OneToOne(cascade = CascadeType.ALL)
	private ExpensePayment payment;

	@OneToOne(cascade = CascadeType.ALL)
	private ExpenseNote note;

	@Enumerated(EnumType.STRING)
	private ExpenseStatus status;

	private String createdBy;
	private Date updated;
	private String updatedBy;
	private boolean deleted;

	@Transient
	private boolean update;
}
