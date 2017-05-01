package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.bean.SearchMerchantRequest;
import com.paycr.common.data.domain.Invoice;
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

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/invoice")
	public List<Invoice> searchInvoices(@RequestBody SearchInvoiceRequest request) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		return serSer.fetchInvoiceList(request);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/merchant")
	public List<Merchant> searchMerchant(@RequestBody SearchMerchantRequest request) {
		return serSer.fetchMerchantList(request);
	}

}
