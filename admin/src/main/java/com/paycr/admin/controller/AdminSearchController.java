package com.paycr.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.admin.service.AdminSearchService;
import com.paycr.common.bean.search.SearchMerchantRequest;
import com.paycr.common.bean.search.SearchPromotionRequest;
import com.paycr.common.bean.search.SearchSubsRequest;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Promotion;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.util.RoleUtil;

@RestController
@RequestMapping("/admin/search")
public class AdminSearchController {

	@Autowired
	private AdminSearchService adminSerSer;

	@PreAuthorize(RoleUtil.PAYCR_AUTH)
	@RequestMapping("/merchant")
	public List<Merchant> searchMerchants(@RequestBody SearchMerchantRequest request) {
		List<Merchant> merchants = adminSerSer.fetchMerchantList(request);
		return merchants;
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/subscription")
	public List<Subscription> searchSubscriptions(@RequestBody SearchSubsRequest request,
			HttpServletResponse response) {
		List<Subscription> subs = adminSerSer.fetchSubsList(request);
		return subs;
	}

	@PreAuthorize(RoleUtil.PAYCR_AUTH)
	@RequestMapping("/promotion")
	public List<Promotion> searchPromotion(@RequestBody SearchPromotionRequest request) {
		List<Promotion> promotions = adminSerSer.fetchPromotionList(request);
		return promotions;
	}

}
