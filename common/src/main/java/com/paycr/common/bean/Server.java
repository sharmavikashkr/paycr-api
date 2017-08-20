package com.paycr.common.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Server {

	@Value("${admin.banner.location}")
	private String adminLocation;

	@Value("${merchant.banner.location}")
	private String merchantLocation;

	@Value("${invoice.pdf.location}")
	private String invoiceLocation;

	@Value("${payment.pdf.location}")
	private String paymentLocation;

	@Value("${subscription.pdf.location}")
	private String subscriptionLocation;

	@Value("${wkhtmltopdf.location}")
	private String wkhtmlToPdfLocation;

	public String getInvoiceLocation() {
		return invoiceLocation;
	}

	public void setInvoiceLocation(String invoiceLocation) {
		this.invoiceLocation = invoiceLocation;
	}

	public String getPaymentLocation() {
		return paymentLocation;
	}

	public void setPaymentLocation(String paymentLocation) {
		this.paymentLocation = paymentLocation;
	}

	public String getWkhtmlToPdfLocation() {
		return wkhtmlToPdfLocation;
	}

	public void setWkhtmlToPdfLocation(String wkhtmlToPdfLocation) {
		this.wkhtmlToPdfLocation = wkhtmlToPdfLocation;
	}

	public String getSubscriptionLocation() {
		return subscriptionLocation;
	}

	public void setSubscriptionLocation(String subscriptionLocation) {
		this.subscriptionLocation = subscriptionLocation;
	}

	public String getAdminLocation() {
		return adminLocation;
	}

	public void setAdminLocation(String adminLocation) {
		this.adminLocation = adminLocation;
	}

	public String getMerchantLocation() {
		return merchantLocation;
	}

	public void setMerchantLocation(String merchantLocation) {
		this.merchantLocation = merchantLocation;
	}

}
