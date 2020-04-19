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

import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;
import com.paycr.common.type.ReportType;
import com.paycr.common.type.TimeRange;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_report")
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String name;
	private String description;

	@Enumerated(EnumType.STRING)
	private TimeRange timeRange;

	@Enumerated(EnumType.STRING)
	private ReportType reportType;

	@Enumerated(EnumType.STRING)
	private PayType payType;

	@Enumerated(EnumType.STRING)
	private PayMode payMode;

	@ManyToOne
	private Merchant merchant;

}
