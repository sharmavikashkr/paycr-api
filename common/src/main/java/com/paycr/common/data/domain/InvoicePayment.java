package com.paycr.common.data.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_invoice_payment")
public class InvoicePayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String invoiceCode;
	private String paymentRefNo;
	private String status;
	private String method;
	private Date paidDate;

	@ManyToOne
	@JsonIgnore
	private Merchant merchant;

	@Enumerated(EnumType.STRING)
	private PayType payType;

	@Column(precision = 10, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	private PayMode payMode;

	private boolean deleted;

}
