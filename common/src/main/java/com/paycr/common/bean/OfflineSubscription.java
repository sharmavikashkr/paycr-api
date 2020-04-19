package com.paycr.common.bean;

import com.paycr.common.type.PayMode;

import lombok.Data;

@Data
public class OfflineSubscription {

	private int merchantId;
	private int pricingId;
	private PayMode payMode;
	private String method;
	private String paymentRefNo;
	private int quantity;

}
