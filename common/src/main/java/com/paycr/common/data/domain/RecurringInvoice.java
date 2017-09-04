package com.paycr.common.data.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.RecurrType;

@Entity
@Table(name = "pc_recurring_invoice")
public class RecurringInvoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private RecurrType recurr;

	private int remaining;
	private int total;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;

	private Date nextInvDate;

	private boolean active;

	@JsonIgnore
	@ManyToOne
	private Invoice invoice;

	public RecurrType getRecurr() {
		return recurr;
	}

	public void setRecurr(RecurrType recurr) {
		this.recurr = recurr;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Date getNextInvDate() {
		return nextInvDate;
	}

	public void setNextInvDate(Date nextInvDate) {
		this.nextInvDate = nextInvDate;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Integer getId() {
		return id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getRemaining() {
		return remaining;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
