package com.paycr.common.data.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paycr.common.type.ParamValueProvider;

@Entity
@Table(name = "pc_merchant_custom_param")
public class MerchantCustomParam {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String paramName;

	@Enumerated(EnumType.STRING)
	private ParamValueProvider provider;

	@JsonIgnore
	@ManyToOne
	private MerchantSetting merchantSetting;

	public ParamValueProvider getProvider() {
		return provider;
	}

	public void setProvider(ParamValueProvider provider) {
		this.provider = provider;
	}

	public Integer getId() {
		return id;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public MerchantSetting getMerchantSetting() {
		return merchantSetting;
	}

	public void setMerchantSetting(MerchantSetting merchantSetting) {
		this.merchantSetting = merchantSetting;
	}

}
