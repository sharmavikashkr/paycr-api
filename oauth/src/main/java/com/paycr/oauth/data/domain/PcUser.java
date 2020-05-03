package com.paycr.oauth.data.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	private boolean active;

	@OneToMany(mappedBy = "pcUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<UserRole> userRoles;

}
