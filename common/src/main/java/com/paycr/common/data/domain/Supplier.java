package com.paycr.common.data.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "pc_supplier")
public class Supplier {

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

	private boolean active;
	private String createdBy;

	@JsonIgnore
	@ManyToOne
	private Merchant merchant;

	@OneToOne(cascade = CascadeType.ALL)
	private Address address;

}
