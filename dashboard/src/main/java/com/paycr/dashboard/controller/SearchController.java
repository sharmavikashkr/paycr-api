package com.paycr.dashboard.controller;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.bean.SearchInvoiceResponse;
import com.paycr.common.bean.SearchMerchantRequest;
import com.paycr.common.bean.SearchMerchantResponse;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private SearchService serSer;

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/invoice")
	public SearchInvoiceResponse searchInvoices(@RequestBody SearchInvoiceRequest request,
			HttpServletResponse response) {
		SearchInvoiceResponse searchResponse = new SearchInvoiceResponse();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			if (CommonUtil.isNotNull(merchant)) {
				request.setMerchant(merchant.getId());
			}
			searchResponse = serSer.fetchInvoiceList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return searchResponse;
	}

	@PreAuthorize(RoleUtil.PAYCR_AUTH)
	@RequestMapping("/merchant")
	public SearchMerchantResponse searchMerchant(@RequestBody SearchMerchantRequest request,
			HttpServletResponse response) {
		SearchMerchantResponse searchResponse = new SearchMerchantResponse();
		try {
			searchResponse = serSer.fetchMerchantList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return searchResponse;
	}

}
