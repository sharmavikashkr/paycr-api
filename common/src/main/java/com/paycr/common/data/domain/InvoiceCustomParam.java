package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.ParamValueProvider;

@Data
@Entity
@Table(name = "pc_invoice_custom_param")
public class InvoiceCustomParam {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String paramName;
	private String paramValue;

	@Enumerated(EnumType.STRING)
	private ParamValueProvider provider;

	@JsonIgnore
	@ManyToOne
	private Invoice invoice;

	@Transient
	private boolean include;

}
