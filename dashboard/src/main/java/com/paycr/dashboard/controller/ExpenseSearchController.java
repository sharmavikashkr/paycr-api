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

import com.paycr.common.bean.search.SearchAssetRequest;
import com.paycr.common.bean.search.SearchExpensePaymentRequest;
import com.paycr.common.bean.search.SearchExpenseRequest;
import com.paycr.common.bean.search.SearchSupplierRequest;
import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Supplier;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.ExpenseSearchService;

@RestController
@RequestMapping("/expense/search")
public class ExpenseSearchController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ExpenseSearchService expSerSer;

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/expense")
	public List<Expense> searchExpenses(@RequestBody SearchExpenseRequest request, HttpServletResponse response) {
		List<Expense> expenseList = new ArrayList<>();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			expenseList = expSerSer.fetchExpenseList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return expenseList;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/payment")
	public List<ExpensePayment> searchPayments(@RequestBody SearchExpensePaymentRequest request,
			HttpServletResponse response) {
		List<ExpensePayment> paymentList = new ArrayList<>();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			paymentList = expSerSer.fetchPaymentList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return paymentList;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/payment/download")
	public void downloadPayments(@RequestBody SearchExpensePaymentRequest request, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			String csv = expSerSer.downloadPayments(request);
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
	public void mailPayments(@RequestBody SearchExpensePaymentRequest request, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			expSerSer.mailPayments(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/supplier")
	public Set<Supplier> searchSuppliers(@RequestBody SearchSupplierRequest request, HttpServletResponse response) {
		Set<Supplier> supplierList = new HashSet<>();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			supplierList = expSerSer.fetchSupplierList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return supplierList;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/asset")
	public List<Asset> searchAsset(@RequestBody SearchAssetRequest request, HttpServletResponse response) {
		List<Asset> assetList = new ArrayList<>();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			assetList = expSerSer.fetchAssetList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return assetList;
	}

}
