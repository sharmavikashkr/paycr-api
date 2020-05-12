package com.paycr.report.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.domain.Schedule;
import com.paycr.common.data.domain.ScheduleHistory;
import com.paycr.common.data.domain.ScheduleUser;
import com.paycr.common.data.repository.ReportRepository;
import com.paycr.common.data.repository.ScheduleHistoryRepository;
import com.paycr.common.data.repository.ScheduleRepository;
import com.paycr.common.data.repository.ScheduleUserRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.report.helper.ReportHelper;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

	@Autowired
	private ReportRepository repRepo;

	@Autowired
	private ScheduleRepository schRepo;

	@Autowired
	private ScheduleUserRepository schUserRepo;

	@Autowired
	private ScheduleHistoryRepository schHistRepo;

	@Autowired
	private ReportHelper repHelp;

	public List<Schedule> getSchedules(PcUser user) {
		return schUserRepo.findByPcUser(user).stream().map(rru -> rru.getSchedule()).collect(Collectors.toList());
	}

	public void addSchedule(Integer reportId, Merchant merchant, PcUser user) {
		Report report = repRepo.findById(reportId).get();
		if (CommonUtil.isNull(report)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Report");
		}
		ScheduleUser scheduleUser = schUserRepo.findByUserAndReport(user, report);
		if (CommonUtil.isNotNull(scheduleUser)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Report already scheduled for you");
		}
		Schedule schedule = schRepo.findByReportAndMerchant(report, merchant);
		if (CommonUtil.isNull(schedule)) {
			schedule = new Schedule();
			schedule.setActive(true);
			schedule.setMerchant(merchant);
			schedule.setReport(report);
			schedule.setStartDate(new Date());
			schedule.setNextDate(new Date());
			schRepo.save(schedule);
		}
		int schedules = schUserRepo.findByPcUser(user).size();
		if (schedules >= 5) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Max 5 reports can be scheduled");
		}
		if (schedule.getNextDate().before(new Date())) {
			Calendar nextDateInUTC = repHelp.getNextDate(report.getTimeRange());
			schedule.setNextDate(nextDateInUTC.getTime());
		}
		scheduleUser = new ScheduleUser();
		scheduleUser.setSchedule(schedule);
		scheduleUser.setPcUser(user);
		schUserRepo.save(scheduleUser);
	}

	public void removeSchedule(Integer reportId, PcUser user) {
		Report report = repRepo.findById(reportId).get();
		ScheduleUser scheduleUser = schUserRepo.findByUserAndReport(user, report);
		if (CommonUtil.isNull(scheduleUser)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Request");
		}
		if (scheduleUser.getPcUser().getId() == user.getId()) {
			schUserRepo.deleteById(scheduleUser.getId());
		} else {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Request");
		}
	}

	public List<ScheduleHistory> getScheduleHistories(Integer scheduleId) {
		Schedule schedule = schRepo.findById(scheduleId).get();
		return schHistRepo.findBySchedule(schedule);
	}

}
