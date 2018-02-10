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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.paycr.common.type.Currency;
import com.paycr.common.type.NoteType;

@Entity
@Table(name = "pc_expense_note")
public class ExpenseNote implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;

	@NotEmpty
	private String noteCode;

	@NotEmpty
	private String expenseCode;

	@NotNull
	private Date noteDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private NoteType noteType;

	@NotNull
	private String noteReason;

	@ManyToOne
	private Merchant merchant;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal total;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal totalPrice;

	@Column(precision = 10, scale = 2)
	private BigDecimal adjustment;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal payAmount;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Currency currency;

	@ManyToOne(cascade = CascadeType.ALL)
	private Supplier supplier;

	@OneToMany(mappedBy = "expenseNote", cascade = CascadeType.ALL)
	private List<ExpenseItem> items;

	private String createdBy;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getNoteCode() {
		return noteCode;
	}

	public void setNoteCode(String noteCode) {
		this.noteCode = noteCode;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Integer getId() {
		return id;
	}

	public BigDecimal getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(BigDecimal adjustment) {
		this.adjustment = adjustment;
	}

	public NoteType getNoteType() {
		return noteType;
	}

	public void setNoteType(NoteType noteType) {
		this.noteType = noteType;
	}

	public String getNoteReason() {
		return noteReason;
	}

	public void setNoteReason(String noteReason) {
		this.noteReason = noteReason;
	}

	public Date getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}

	public String getExpenseCode() {
		return expenseCode;
	}

	public void setExpenseCode(String expenseCode) {
		this.expenseCode = expenseCode;
	}

	public List<ExpenseItem> getItems() {
		return items;
	}

	public void setItems(List<ExpenseItem> items) {
		this.items = items;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

}
