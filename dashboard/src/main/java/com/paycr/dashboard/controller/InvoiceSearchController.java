package com.paycr.dashboard.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.search.SearchConsumerRequest;
import com.paycr.common.bean.search.SearchInventoryRequest;
import com.paycr.common.bean.search.SearchInvoicePaymentRequest;
import com.paycr.common.bean.search.SearchInvoiceRequest;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.InvoiceSearchService;

@RestController
@RequestMapping("/invoice/search")
public class InvoiceSearchController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private InvoiceSearchService invSerSer;

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/invoice")
	public List<Invoice> searchInvoices(@RequestBody SearchInvoiceRequest request, HttpServletResponse response) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		List<Invoice> invoiceList = invSerSer.fetchInvoiceList(request);
		return invoiceList;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/payment")
	public List<InvoicePayment> searchPayments(@RequestBody SearchInvoicePaymentRequest request,
			HttpServletResponse response) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		List<InvoicePayment> paymentList = invSerSer.fetchPaymentList(request);
		return paymentList;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/consumer")
	public Set<Consumer> searchConsumers(@RequestBody SearchConsumerRequest request, HttpServletResponse response) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		Set<Consumer> consumerList = invSerSer.fetchConsumerList(request);
		return consumerList;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/payment/download")
	public void downloadPayments(@RequestBody SearchInvoicePaymentRequest request, HttpServletResponse response)
			throws IOException {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		String csv = invSerSer.downloadPayments(request);
		response.setContentType("text/csv");
		byte[] data = csv.getBytes();
		response.setHeader("Content-Disposition", "attachment; filename=\"payments.csv\"");
		response.setContentType("text/csv;charset=utf-8");
		InputStream is = new ByteArrayInputStream(data);
		IOUtils.copy(is, response.getOutputStream());
		response.setContentLength(data.length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/payment/mail")
	public void mailPayments(@RequestBody SearchInvoicePaymentRequest request, HttpServletResponse response)
			throws IOException {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		invSerSer.mailPayments(request);
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/inventory")
	public List<Inventory> searchInventory(@RequestBody SearchInventoryRequest request, HttpServletResponse response) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		List<Inventory> inventoryList = invSerSer.fetchInventoryList(request);
		return inventoryList;
	}

}
