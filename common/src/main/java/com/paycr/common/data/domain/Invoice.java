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

	@Column(precision = 10, scale = 2)
	private BigDecimal total;

	@Column(precision = 10, scale = 2)
	private BigDecimal payAmount;

	private String taxName;

	@Column(precision = 10, scale = 2)
	private Float taxValue;

	@Column(precision = 10, scale = 2)
	private BigDecimal discount;

	private boolean addItems;
	private Date expiry;

	@Enumerated(EnumType.STRING)
	private Currency currency;

	@ManyToOne(cascade = CascadeType.ALL)
	private Consumer consumer;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<Item> items;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<InvoiceCustomParam> customParams;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<InvoiceNotify> invoiceNotices;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<Attachment> attachments;

	@ManyToOne
	private MerchantPricing merchantPricing;

	@OneToOne(cascade = CascadeType.ALL)
	private Payment payment;

	@Enumerated(EnumType.STRING)
	private InvoiceStatus status;

	private String createdBy;

	private int expiresIn;

	private boolean neverExpire;

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

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
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

	public List<InvoiceNotify> getInvoiceNotices() {
		return invoiceNotices;
	}

	public void setInvoiceNotices(List<InvoiceNotify> invoiceNotices) {
		this.invoiceNotices = invoiceNotices;
	}

	public String getTaxName() {
		return taxName;
	}

	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	public Float getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(Float taxValue) {
		this.taxValue = taxValue;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
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

	public boolean isNeverExpire() {
		return neverExpire;
	}

	public void setNeverExpire(boolean neverExpire) {
		this.neverExpire = neverExpire;
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
}
