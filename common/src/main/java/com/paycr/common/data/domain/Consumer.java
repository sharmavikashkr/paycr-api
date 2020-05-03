package com.paycr.common.data.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.ConsumerType;

@Data
@Entity
@Table(name = "pc_consumer")
public class Consumer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;

	@NotEmpty
	private String name;

	@NotEmpty
	private String email;

	@NotEmpty
	private String mobile;

	private String gstin;
	private boolean emailOnPay;
	private boolean emailOnRefund;
	private boolean emailNote;

	@Enumerated(EnumType.STRING)
	private ConsumerType type;

	private boolean active;
	private String createdBy;

	@OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL)
	private List<ConsumerFlag> flags;

	@JsonIgnore
	@ManyToOne
	private Merchant merchant;

	@OneToOne(cascade = CascadeType.ALL)
	private Address billingAddress;

	@OneToOne(cascade = CascadeType.ALL)
	private Address shippingAddress;

}
