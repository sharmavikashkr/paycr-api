package com.paycr.common.bean;

public class OfflineSubscription {

	private int merchantId;
	private int pricingId;
	private int subscriptionModeId;
	private String paymentRefNo;

	public int getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(int merchantId) {
		this.merchantId = merchantId;
	}

	public int getPricingId() {
		return pricingId;
	}

	public void setPricingId(int pricingId) {
		this.pricingId = pricingId;
	}

	public String getPaymentRefNo() {
		return paymentRefNo;
	}

	public void setPaymentRefNo(String paymentRefNo) {
		this.paymentRefNo = paymentRefNo;
	}

	public int getSubscriptionModeId() {
		return subscriptionModeId;
	}

	public void setSubscriptionModeId(int subscriptionModeId) {
		this.subscriptionModeId = subscriptionModeId;
	}

}
