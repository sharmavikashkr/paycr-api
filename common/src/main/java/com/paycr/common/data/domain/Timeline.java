package com.paycr.common.data.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.paycr.common.type.ObjectType;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_timeline")
public class Timeline {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private Integer objectId;

	@Enumerated(EnumType.STRING)
	private ObjectType objectType;

	private boolean internal;

	private String message;
	private String createdBy;

}
