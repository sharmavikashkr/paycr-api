package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.StatsResponse;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.CommonService;

@RestController
@RequestMapping("/common")
public class CommonController {

	@Autowired
	private CommonService comSer;

	@PreAuthorize(RoleUtil.ALL_ADMIN_AUTH)
	@RequestMapping("/dashboard/{timeRange}")
	public StatsResponse getDashboard(@PathVariable String timeRange) {
		return comSer.loadDashboard(timeRange);
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/notifications")
	public List<Notification> getNotifications() {
		return comSer.getNotifications();
	}

	@PreAuthorize(RoleUtil.ALL_FINANCE_AUTH)
	@RequestMapping("/pricings")
	public List<Pricing> getPricings() {
		return comSer.getPricings();
	}

}
