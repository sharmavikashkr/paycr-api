package com.paycr.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.admin.service.AdminSearchService;
import com.paycr.common.bean.SearchMerchantRequest;
import com.paycr.common.bean.SearchSubsRequest;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.util.RoleUtil;

@RestController
@RequestMapping("/admin/search")
public class AdminSearchController {

	@Autowired
	private AdminSearchService adminSerSer;

	@PreAuthorize(RoleUtil.PAYCR_AUTH)
	@RequestMapping("/merchant")
	public List<Merchant> searchMerchants(@RequestBody SearchMerchantRequest request, HttpServletResponse response) {
		List<Merchant> merchants = new ArrayList<>();
		try {
			merchants = adminSerSer.fetchMerchantList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return merchants;
	}

	@PreAuthorize(RoleUtil.PAYCR_FINANCE_AUTH)
	@RequestMapping("/subscription")
	public List<Subscription> searchSubscriptions(@RequestBody SearchSubsRequest request,
			HttpServletResponse response) {
		List<Subscription> subs = new ArrayList<>();
		try {
			subs = adminSerSer.fetchSubsList(request);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return subs;
	}

}
