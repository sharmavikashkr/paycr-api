package com.paycr.common.data.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "pc_invoice_notify")
public class InvoiceNotify implements Serializable {

	private static final long serialVersionUID = -8798244987005274799L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;

	@ManyToOne
	@JsonIgnore
	private Invoice invoice;

	private boolean sendEmail;
	private boolean sendSms;
	private boolean emailPdf;
	private boolean ccMe;
	private String ccEmail;

	@NotEmpty
	private String emailNote;

	@NotEmpty
	private String emailSubject;

}
