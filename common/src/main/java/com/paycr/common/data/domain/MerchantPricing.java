package com.paycr.common.data.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.PricingStatus;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_merchant_pricing")
public class MerchantPricing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private Date startDate;
	private Date endDate;
	private int quantity;

	@Enumerated(EnumType.STRING)
	private PricingStatus status;

	@JsonIgnore
	@ManyToOne
	private Merchant merchant;

	@OneToOne
	private Pricing pricing;

	@JsonIgnore
	@OneToOne
	private Subscription subscription;

	private int useCount;

}
