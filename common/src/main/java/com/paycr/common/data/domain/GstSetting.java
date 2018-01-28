package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.paycr.common.type.FilingPeriod;

@Entity
@Table(name = "pc_gst_setting")
public class GstSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private FilingPeriod filingPeriod;

	private boolean invCreated;
	private boolean invUnpaid;
	private boolean invPaid;
	private boolean invExpired;
	private boolean invDeclined;

	private boolean expUnpaid;
	private boolean expPaid;

	public boolean isInvCreated() {
		return invCreated;
	}

	public void setInvCreated(boolean invCreated) {
		this.invCreated = invCreated;
	}

	public boolean isInvUnpaid() {
		return invUnpaid;
	}

	public void setInvUnpaid(boolean invUnpaid) {
		this.invUnpaid = invUnpaid;
	}

	public boolean isInvPaid() {
		return invPaid;
	}

	public void setInvPaid(boolean invPaid) {
		this.invPaid = invPaid;
	}

	public boolean isInvExpired() {
		return invExpired;
	}

	public void setInvExpired(boolean invExpired) {
		this.invExpired = invExpired;
	}

	public boolean isInvDeclined() {
		return invDeclined;
	}

	public void setInvDeclined(boolean invDeclined) {
		this.invDeclined = invDeclined;
	}

	public boolean isExpUnpaid() {
		return expUnpaid;
	}

	public void setExpUnpaid(boolean expUnpaid) {
		this.expUnpaid = expUnpaid;
	}

	public boolean isExpPaid() {
		return expPaid;
	}

	public void setExpPaid(boolean expPaid) {
		this.expPaid = expPaid;
	}

	public Integer getId() {
		return id;
	}

	public FilingPeriod getFilingPeriod() {
		return filingPeriod;
	}

	public void setFilingPeriod(FilingPeriod filingPeriod) {
		this.filingPeriod = filingPeriod;
	}

}
