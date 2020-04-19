package com.paycr.common.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class Server {

	@Value("${admin.banner.location}")
	private String adminLocation;

	@Value("${merchant.banner.location}")
	private String merchantLocation;

	@Value("${invoice.attachment.location}")
	private String invAttachLocation;

	@Value("${expense.attachment.location}")
	private String expAttachLocation;

	@Value("${bulk.invoice.location}")
	private String bulkInvoiceLocation;

	@Value("${bulk.consumer.location}")
	private String bulkConsumerLocation;

	@Value("${bulk.supplier.location}")
	private String bulkSupplierLocation;

	@Value("${bulk.inventory.location}")
	private String bulkInventoryLocation;

	@Value("${bulk.asset.location}")
	private String bulkAssetLocation;

	@Value("${report.pdf.location}")
	private String reportLocation;

	@Value("${invoice.pdf.location}")
	private String invoiceLocation;

	@Value("${expense.pdf.location}")
	private String expenseLocation;

	@Value("${payment.pdf.location}")
	private String paymentLocation;

	@Value("${merchant.gst.location}")
	private String gstLocation;

	@Value("${subscription.pdf.location}")
	private String subscriptionLocation;

	@Value("${wkhtmltopdf.location}")
	private String wkhtmlToPdfLocation;

}
