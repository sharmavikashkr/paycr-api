package com.paycr.common.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class Server {

	@Value("${admin.location.banner}")
	private String adminLocation;

	@Value("${merchant.location.banner}")
	private String merchantLocation;

	@Value("${invoice.location.attachment}")
	private String invAttachLocation;

	@Value("${expense.location.attachment}")
	private String expAttachLocation;

	@Value("${bulk.location.invoice}")
	private String bulkInvoiceLocation;

	@Value("${bulk.location.consumer}")
	private String bulkConsumerLocation;

	@Value("${bulk.location.supplier}")
	private String bulkSupplierLocation;

	@Value("${bulk.location.inventory}")
	private String bulkInventoryLocation;

	@Value("${bulk.location.asset}")
	private String bulkAssetLocation;

	@Value("${report.location.pdf}")
	private String reportLocation;

	@Value("${invoice.location.pdf}")
	private String invoiceLocation;

	@Value("${expense.location.pdf}")
	private String expenseLocation;

	@Value("${payment.location.pdf}")
	private String paymentLocation;

	@Value("${merchant.location.gst}")
	private String gstLocation;

	@Value("${subscription.location.pdf}")
	private String subscriptionLocation;

	@Value("${wkhtmltopdf.location}")
	private String wkhtmlToPdfLocation;

}
