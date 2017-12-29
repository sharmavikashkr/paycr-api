package com.paycr.common.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Server {

	@Value("${admin.banner.location}")
	private String adminLocation;

	@Value("${merchant.banner.location}")
	private String merchantLocation;

	@Value("${bulk.invoice.location}")
	private String bulkInvoiceLocation;

	@Value("${bulk.consumer.location}")
	private String bulkConsumerLocation;

	@Value("${bulk.inventory.location}")
	private String bulkInventoryLocation;

	@Value("${report.pdf.location}")
	private String reportLocation;

	@Value("${invoice.pdf.location}")
	private String invoiceLocation;

	@Value("${expense.pdf.location}")
	private String expenseLocation;

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

	public String getReportLocation() {
		return reportLocation;
	}

	public void setReportLocation(String reportLocation) {
		this.reportLocation = reportLocation;
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

	public String getBulkConsumerLocation() {
		return bulkConsumerLocation;
	}

	public void setBulkConsumerLocation(String bulkConsumerLocation) {
		this.bulkConsumerLocation = bulkConsumerLocation;
	}

	public String getBulkInvoiceLocation() {
		return bulkInvoiceLocation;
	}

	public void setBulkInvoiceLocation(String bulkInvoiceLocation) {
		this.bulkInvoiceLocation = bulkInvoiceLocation;
	}

	public String getBulkInventoryLocation() {
		return bulkInventoryLocation;
	}

	public void setBulkInventoryLocation(String bulkInventoryLocation) {
		this.bulkInventoryLocation = bulkInventoryLocation;
	}

	public String getExpenseLocation() {
		return expenseLocation;
	}

	public void setExpenseLocation(String expenseLocation) {
		this.expenseLocation = expenseLocation;
	}

}
