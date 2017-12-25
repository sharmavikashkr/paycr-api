package com.paycr.dashboard.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.StatsResponse;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.domain.Timeline;
import com.paycr.common.type.ObjectType;
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

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/taxes")
	public List<TaxMaster> getTaxes() {
		return comSer.getTaxes();
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/timeline/{objectType}/{objectId}")
	public List<Timeline> getTimelines(@PathVariable ObjectType objectType, @PathVariable Integer objectId) {
		return comSer.getTimelines(objectType, objectId);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/timeline/new", method = RequestMethod.PUT)
	public void addComment(@RequestBody Timeline timeline, HttpServletResponse response) {
		try {
			comSer.saveComment(timeline);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

}
