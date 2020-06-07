package com.paycr.merchant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.StatsResponse;
import com.paycr.common.util.RoleUtil;
import com.paycr.merchant.service.MerchantDashboardService;

@RestController
@RequestMapping("/merchant")
public class MerchantDashboardController {

	@Autowired
	private MerchantDashboardService merDashSer;

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@GetMapping("/dashboard/{timeRange}")
	public StatsResponse getDashboard(@PathVariable String timeRange) {
		return merDashSer.loadDashboard(timeRange);
	}

}
