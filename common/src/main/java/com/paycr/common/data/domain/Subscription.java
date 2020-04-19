package com.paycr.common.data.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.paycr.common.type.Currency;
import com.paycr.common.type.PayMode;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_subscription")
public class Subscription implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String subscriptionCode;
	private Date created;
	private BigDecimal total;

	@OneToOne(cascade = CascadeType.ALL)
	private TaxMaster tax;

	private BigDecimal payAmount;
	private int quantity;

	@Enumerated(EnumType.STRING)
	private Currency currency;

	private String paymentRefNo;
	private String status;
	private String method;

	@ManyToOne
	private Pricing pricing;

	@ManyToOne
	private Merchant merchant;

	@Enumerated(EnumType.STRING)
	private PayMode payMode;

}
