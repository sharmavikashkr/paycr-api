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

	@Transient
	private boolean update;

	public Integer getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public ExpensePayment getPayment() {
		return payment;
	}

	public void setPayment(ExpensePayment payment) {
		this.payment = payment;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public boolean isAddItems() {
		return addItems;
	}

	public void setAddItems(boolean addItems) {
		this.addItems = addItems;
	}

	public List<ExpenseAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<ExpenseAttachment> attachments) {
		this.attachments = attachments;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public BigDecimal getShipping() {
		return shipping;
	}

	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getExpenseCode() {
		return expenseCode;
	}

	public void setExpenseCode(String expenseCode) {
		this.expenseCode = expenseCode;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<ExpenseItem> getItems() {
		return items;
	}

	public void setItems(List<ExpenseItem> items) {
		this.items = items;
	}

	public ExpenseStatus getStatus() {
		return status;
	}

	public void setStatus(ExpenseStatus status) {
		this.status = status;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public MerchantPricing getMerchantPricing() {
		return merchantPricing;
	}

	public void setMerchantPricing(MerchantPricing merchantPricing) {
		this.merchantPricing = merchantPricing;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public ExpenseNote getNote() {
		return note;
	}

	public void setNote(ExpenseNote note) {
		this.note = note;
	}
}
