package com.paycr.common.data.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.paycr.common.type.InvoiceType;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_bulk_flag")
public class BulkFlag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;

	private String flags;

	@Enumerated(EnumType.STRING)
	private InvoiceType invoiceType;

	private String invoiceCode;

	private String message;

	private String createdBy;

}
