package com.paycr.common.data.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
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
}
