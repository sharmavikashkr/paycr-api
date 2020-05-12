package com.paycr.report.controller;

import java.util.List;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Schedule;
import com.paycr.common.data.domain.ScheduleHistory;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.report.service.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ScheduleService schSer;

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@GetMapping("/get")
	public List<Schedule> getSchedules() {
		final PcUser user = secSer.findLoggedInUser();
		return schSer.getSchedules(user);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@GetMapping("/add/{reportId}")
	public void addSchedule(@PathVariable final Integer reportId) {
		final Merchant merchant = secSer.getMerchantForLoggedInUser();
		final PcUser user = secSer.findLoggedInUser();
		schSer.addSchedule(reportId, merchant, user);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@DeleteMapping("/remove/{reportId}")
	public void removeSchedule(@PathVariable final Integer reportId) {
		final PcUser user = secSer.findLoggedInUser();
		schSer.removeSchedule(reportId, user);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@GetMapping("/history/{scheduleId}")
	public List<ScheduleHistory> getScheduleHistory(@PathVariable final Integer scheduleId) {
		return schSer.getScheduleHistories(scheduleId);
	}

}
