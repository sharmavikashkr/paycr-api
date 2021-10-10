package com.paycr.expense.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.paycr.expense.service.ExpenseSearchService;

@RestController
@RequestMapping("/expense/search")
public class ExpenseSearchController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ExpenseSearchService expSerSer;

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@PostMapping("/expense")
	public List<Expense> searchExpenses(@RequestBody SearchExpenseRequest request) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		List<Expense> expenseList = expSerSer.fetchExpenseList(request);
		return expenseList;
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@PostMapping("/payment")
	public List<ExpensePayment> searchPayments(@RequestBody SearchExpensePaymentRequest request,
			HttpServletResponse response) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		List<ExpensePayment> paymentList = expSerSer.fetchPaymentList(request);
		return paymentList;
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@PostMapping("/payment/download")
	public void downloadPayments(@RequestBody SearchExpensePaymentRequest request, HttpServletResponse response)
			throws IOException {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		String csv = expSerSer.downloadPayments(request);
		response.setContentType("text/csv");
		byte[] data = csv.getBytes();
		response.setHeader("Content-Disposition", "attachment; filename=\"payments.csv\"");
		response.setContentType("text/csv;charset=utf-8");
		response.getOutputStream().write(data);
		response.setContentLength(data.length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@PostMapping("/payment/mail")
	public void mailPayments(@RequestBody SearchExpensePaymentRequest request) throws IOException {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		expSerSer.mailPayments(request, secSer.findLoggedInUser());
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@PostMapping("/supplier")
	public Set<Supplier> searchSuppliers(@RequestBody SearchSupplierRequest request) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		Set<Supplier> supplierList = expSerSer.fetchSupplierList(request);
		return supplierList;
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@PostMapping("/asset")
	public List<Asset> searchAsset(@RequestBody SearchAssetRequest request) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		List<Asset> assetList = expSerSer.fetchAssetList(request);
		return assetList;
	}

}
