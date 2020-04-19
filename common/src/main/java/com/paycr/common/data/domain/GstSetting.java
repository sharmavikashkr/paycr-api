package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.paycr.common.type.FilingPeriod;

import lombok.Data;

@Data
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
}
