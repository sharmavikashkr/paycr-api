package com.paycr.common.data.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.RecurrType;

@Data
@Entity
@Table(name = "pc_recurring_invoice")
public class RecurringInvoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private RecurrType recurr;

	private int remaining;
	private int total;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;

	private Date nextDate;

	private boolean active;

	@JsonIgnore
	@ManyToOne
	private Invoice invoice;

}
