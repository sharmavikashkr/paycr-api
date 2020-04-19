package com.paycr.common.data.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_merchant")
public class Merchant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String name;
	private String email;
	private String mobile;
	private String accessKey;

	@JsonIgnore
	private String secretKey;

	private String gstin;
	private boolean active;
	private String banner;

	@OneToOne(cascade = CascadeType.ALL)
	private Address address;

	@OneToMany(mappedBy = "merchant")
	private List<MerchantPricing> pricings;

	@OneToOne(cascade = CascadeType.ALL)
	private InvoiceSetting invoiceSetting;

	@OneToOne(cascade = CascadeType.ALL)
	private PaymentSetting paymentSetting;

	@OneToOne(cascade = CascadeType.ALL)
	private GstSetting gstSetting;
}
