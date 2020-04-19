package com.paycr.common.bean;

import lombok.Data;

@Data
public class DailyPay {

	private String created;
	private Double salePaySum;
	private Double refundPaySum;

}
