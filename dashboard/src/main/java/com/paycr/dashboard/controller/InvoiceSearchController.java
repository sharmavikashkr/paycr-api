package com.paycr.dashboard.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
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
		List<Invoice> invoiceList = new ArrayList<>();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			invoiceList = invSerSer.fetchInvoiceList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return invoiceList;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/payment")
	public List<InvoicePayment> searchPayments(@RequestBody SearchInvoicePaymentRequest request,
			HttpServletResponse response) {
		List<InvoicePayment> paymentList = new ArrayList<>();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			paymentList = invSerSer.fetchPaymentList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return paymentList;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/consumer")
	public Set<Consumer> searchConsumers(@RequestBody SearchConsumerRequest request, HttpServletResponse response) {
		Set<Consumer> consumerList = new HashSet<>();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			consumerList = invSerSer.fetchConsumerList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return consumerList;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/payment/download")
	public void downloadPayments(@RequestBody SearchInvoicePaymentRequest request, HttpServletResponse response) {
		try {
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
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/payment/mail")
	public void mailPayments(@RequestBody SearchInvoicePaymentRequest request, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			invSerSer.mailPayments(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/inventory")
	public List<Inventory> searchInventory(@RequestBody SearchInventoryRequest request, HttpServletResponse response) {
		List<Inventory> inventoryList = new ArrayList<>();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			inventoryList = invSerSer.fetchInventoryList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return inventoryList;
	}

}
