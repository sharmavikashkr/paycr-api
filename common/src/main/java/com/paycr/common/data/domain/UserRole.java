package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.paycr.common.type.Role;

@Entity
@Table(name = "pc_user_role")
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	private PcUser pcUser;

	@Enumerated(EnumType.STRING)
	private Role role;

	public PcUser getPcUser() {
		return pcUser;
	}

	public void setPcUser(PcUser pcUser) {
		this.pcUser = pcUser;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Integer getId() {
		return id;
	}

}
