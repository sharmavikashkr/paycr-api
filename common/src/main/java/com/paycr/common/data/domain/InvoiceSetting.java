package com.paycr.common.data.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "pc_invoice_setting")
public class InvoiceSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private boolean sendSms;
	private boolean sendEmail;
	private boolean addItems;
	private boolean emailPdf;
	private boolean refundCreditNote;
	private boolean ccMe;
	private boolean autoRemind;
	private int remindDays;
	private int expiryDays;
	private String emailNote;
	private String emailSubject;

	@OneToMany(mappedBy = "invoiceSetting", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MerchantCustomParam> customParams;

}
