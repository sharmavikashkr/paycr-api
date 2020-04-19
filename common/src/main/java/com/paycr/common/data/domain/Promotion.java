package com.paycr.common.data.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_promotion")
public class Promotion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String name;
	private String email;
	private String phone;
	private boolean sent;
	private int notified;
	private String createdBy;

	@OneToOne(cascade = CascadeType.ALL)
	private Address address;

}
