package com.payme.common.data.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.payme.common.type.ResetStatus;

@Entity
@Table(name = "pm_reset_password")
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getResetCode() {
		return resetCode;
	}

	public void setResetCode(String resetCode) {
		this.resetCode = resetCode;
	}

	public Integer getId() {
		return id;
	}

	public ResetStatus getStatus() {
		return status;
	}

	public void setStatus(ResetStatus status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
