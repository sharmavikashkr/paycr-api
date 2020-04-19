package com.paycr.common.data.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_contact_us")
public class ContactUs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date created;

	@NotEmpty
	private String name;

	@NotEmpty
	private String email;

	@NotEmpty
	private String type;

	private boolean resolved;

	@NotEmpty
	private String subject;

	@NotEmpty
	@Lob
	private String message;
}
