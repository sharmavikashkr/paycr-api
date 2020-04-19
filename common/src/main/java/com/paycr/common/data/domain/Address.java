package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;

import lombok.Data;

import com.paycr.common.type.AddressType;

@Data
@Entity
@Table(name = "pc_address")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotEmpty
	private String addressLine1;

	private String addressLine2;

	@NotEmpty
	private String city;

	@NotEmpty
	private String state;

	@NotEmpty
	private String country;

	@NotEmpty
	private String pincode;

	@Transient
	private AddressType type;

}
