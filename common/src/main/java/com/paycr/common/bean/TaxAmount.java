package com.paycr.common.bean;

import java.math.BigDecimal;

import com.paycr.common.data.domain.TaxMaster;

import lombok.Data;

@Data
public class TaxAmount {

	private TaxMaster tax;

	private BigDecimal amount;

	private BigDecimal taxableAmount;

}
