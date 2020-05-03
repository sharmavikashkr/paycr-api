package com.paycr.common.data.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

import com.paycr.common.type.Currency;
import com.paycr.common.type.NoteType;

@Data
@Entity
@Table(name = "pc_invoice_note")
public class InvoiceNote implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String noteCode;

	@NotEmpty
	private String invoiceCode;

	@NotNull
	private Date noteDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private NoteType noteType;

	@NotNull
	private String noteReason;

	private boolean refundCreditNote;

	@ManyToOne
	private Merchant merchant;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal total;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal totalPrice;

	@Column(precision = 10, scale = 2)
	private BigDecimal adjustment;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal payAmount;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Currency currency;

	@ManyToOne(cascade = CascadeType.ALL)
	private Consumer consumer;

	@OneToMany(mappedBy = "invoiceNote", cascade = CascadeType.ALL)
	private List<InvoiceItem> items;

	private String createdBy;
	private boolean deleted;

}
