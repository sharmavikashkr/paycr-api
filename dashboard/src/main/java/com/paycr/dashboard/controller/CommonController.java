package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.StatsRequest;
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
	@RequestMapping("/dashboard")
	public StatsResponse getDashboard(@RequestBody StatsRequest request) {
		return comSer.loadDashboard(request);
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
