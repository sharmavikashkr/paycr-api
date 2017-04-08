package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.paycr.common.bean.PaycrResponse;
import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.data.dao.InvoiceDao;
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
	private InvoiceDao invDao;

	@Autowired
	private SearchService serSer;

	@Secured({ "ROLE_MERCHANT, ROLE_ADMIN" })
	@RequestMapping("/invoice")
	public PaycrResponse searchInvoices(@RequestBody SearchInvoiceRequest request) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		if (CommonUtil.isNotNull(merchant)) {
			request.setMerchant(merchant.getId());
		}
		List<Invoice> invoiceList = invDao.findInvoices(request);
		PaycrResponse resp = new PaycrResponse();
		resp.setRespCode(0);
		resp.setRespMsg("SUCCESS");
		resp.setData(new Gson().toJson(serSer.parseInvoiceList(invoiceList)));
		return resp;
	}

}
