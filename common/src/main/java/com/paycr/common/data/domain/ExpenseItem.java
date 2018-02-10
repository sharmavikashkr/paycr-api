package com.paycr.common.data.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pc_expense_item")
public class ExpenseItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private int quantity;
	private BigDecimal price;

	@ManyToOne
	private TaxMaster tax;

	@JsonIgnore
	@ManyToOne
	private Expense expense;

	@JsonIgnore
	@ManyToOne
	private ExpenseNote expenseNote;

	@ManyToOne
	private Asset asset;

	public Integer getId() {
		return id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public TaxMaster getTax() {
		return tax;
	}

	public void setTax(TaxMaster tax) {
		this.tax = tax;
	}

	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public ExpenseNote getExpenseNote() {
		return expenseNote;
	}

	public void setExpenseNote(ExpenseNote expenseNote) {
		this.expenseNote = expenseNote;
	}

}
