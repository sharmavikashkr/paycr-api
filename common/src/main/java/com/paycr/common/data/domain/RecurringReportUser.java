package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pc_recurring_report_user")
public class RecurringReportUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	private RecurringReport recurringReport;

	@JsonIgnore
	@ManyToOne
	private PcUser pcUser;

	public RecurringReport getRecurringReport() {
		return recurringReport;
	}

	public void setRecurringReport(RecurringReport recurringReport) {
		this.recurringReport = recurringReport;
	}

	public PcUser getPcUser() {
		return pcUser;
	}

	public void setPcUser(PcUser pcUser) {
		this.pcUser = pcUser;
	}

	public Integer getId() {
		return id;
	}

}
