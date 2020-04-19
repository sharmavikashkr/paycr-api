package com.paycr.common.data.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.paycr.common.type.ResetStatus;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_reset_password")
public class ResetPassword implements Serializable {

	private static final long serialVersionUID = -7951712267210815427L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String resetCode;
	private String email;

	@Enumerated(EnumType.STRING)
	private ResetStatus status;

}
