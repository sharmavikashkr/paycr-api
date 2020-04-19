package com.paycr.common.data.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.bean.Access;
import com.paycr.common.type.UserType;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_user")
public class PcUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;
	private String name;
	private String email;

	@JsonIgnore
	private String password;

	private String mobile;
	private String createdBy;

	@Enumerated(EnumType.STRING)
	private UserType userType;

	@OneToOne(cascade = CascadeType.ALL)
	private Address address;

	private boolean active;

	@OneToMany(mappedBy = "pcUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<UserRole> userRoles;

	@Transient
	private Access access;

}
