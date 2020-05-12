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
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.ScheduleStatus;

@Data
@Entity
@Table(name = "pc_schedule_history")
public class ScheduleHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date fromDate;
	private Date toDate;
	private Date created;

	@Enumerated(EnumType.STRING)
	private ScheduleStatus status;

	@JsonIgnore
	@ManyToOne
	private Schedule schedule;

}
