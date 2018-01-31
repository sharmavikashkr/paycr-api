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

	@JsonIgnore
	@ManyToOne
	private Invoice parent;

	@Transient
	private boolean update;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	public List<InvoiceItem> getItems() {
		return items;
	}

	public void setItems(List<InvoiceItem> items) {
		this.items = items;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public InvoicePayment getPayment() {
		return payment;
	}

	public void setPayment(InvoicePayment payment) {
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

	public InvoiceStatus getStatus() {
		return status;
	}

	public void setStatus(InvoiceStatus status) {
		this.status = status;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public MerchantPricing getMerchantPricing() {
		return merchantPricing;
	}

	public void setMerchantPricing(MerchantPricing merchantPricing) {
		this.merchantPricing = merchantPricing;
	}

	public List<InvoiceCustomParam> getCustomParams() {
		return customParams;
	}

	public void setCustomParams(List<InvoiceCustomParam> customParams) {
		this.customParams = customParams;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
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

	public List<InvoiceAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<InvoiceAttachment> attachments) {
		this.attachments = attachments;
	}

	public Invoice getParent() {
		return parent;
	}

	public void setParent(Invoice parent) {
		this.parent = parent;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
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

	public List<InvoiceNotify> getNotices() {
		return notices;
	}

	public void setNotices(List<InvoiceNotify> notices) {
		this.notices = notices;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public InvoiceNote getNote() {
		return note;
	}

	public void setNote(InvoiceNote note) {
		this.note = note;
	}
}
