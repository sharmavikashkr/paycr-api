package com.paycr.dashboard.controller;

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
import com.paycr.dashboard.service.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private SearchService serSer;

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/invoice")
	public SearchInvoiceResponse searchInvoices(@RequestBody SearchInvoiceRequest request) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		return serSer.fetchInvoiceList(request);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/merchant")
	public SearchMerchantResponse searchMerchant(@RequestBody SearchMerchantRequest request) {
		return serSer.fetchMerchantList(request);
	}

}
