package com.paycr.scheduler.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paycr.common.bean.DateFilter;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Schedule;
import com.paycr.common.data.domain.ScheduleHistory;
import com.paycr.common.data.domain.ScheduleUser;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.ScheduleHistoryRepository;
import com.paycr.common.data.repository.ScheduleRepository;
import com.paycr.common.data.repository.ScheduleUserRepository;
import com.paycr.common.type.ScheduleStatus;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.ReportHelper;
import com.paycr.report.service.ReportService;

@Service
public class ReportSchedulerService {

	private static final Logger logger = LoggerFactory.getLogger(ReportSchedulerService.class);

	@Autowired
	private ScheduleRepository scheduleRepo;

	@Autowired
	private ScheduleUserRepository scheduleUserRepo;

	@Autowired
	private ScheduleHistoryRepository schHistRepo;

	@Autowired
	private ReportService repSer;

	@Autowired
	private ReportHelper repHelp;

	@Transactional
	public void recurrReports() {
		Date timeNow = new Date();
		Date start = DateUtil.getStartOfDay(timeNow);
		Date end = DateUtil.getEndOfDay(timeNow);
		List<Schedule> scheduleList = scheduleRepo.findTodaysSchedules(start, end);
		ExecutorService exec = Executors.newFixedThreadPool(5);
		scheduleList.forEach(s -> {
			exec.execute(processReport(s));
		});
	}

	@Transactional
	public void reinitOldReports() {
		Date today = DateUtil.getStartOfDay(new Date());
		List<Schedule> scheduleList = scheduleRepo.findOldSchedules(today);
		ExecutorService exec = Executors.newFixedThreadPool(5);
		scheduleList.forEach(s -> {
			exec.execute(reinitReport(s));
		});
	}

	public Runnable processReport(Schedule schedule) {
		return () -> {
			Report report = schedule.getReport();
			Merchant merchant = schedule.getMerchant();
			ScheduleHistory schHist = new ScheduleHistory();
			schHist.setSchedule(schedule);
			DateFilter dateFilter = repHelp.getDateFilter(report.getTimeRange());
			schHist.setFromDate(dateFilter.getStartDate());
			schHist.setToDate(dateFilter.getEndDate());
			try {
				List<String> mailTo = new ArrayList<>();
				List<ScheduleUser> scheduleUsers = scheduleUserRepo.findBySchedule(schedule);
				scheduleUsers.forEach(su -> {
					mailTo.add(su.getPcUser().getEmail());
				});
				repSer.mailReport(report, merchant, mailTo);
				Calendar nextDate = repHelp.getNextDate(report.getTimeRange());
				schedule.setNextDate(nextDate.getTime());
				scheduleRepo.save(schedule);
				schHist.setStatus(ScheduleStatus.SUCCESS);
				schHistRepo.save(schHist);
			} catch (IOException ex) {
				logger.error("Execption while running Schedule : {} ", schedule.getId(), ex);
				schHist.setStatus(ScheduleStatus.FAILED);
				schHistRepo.save(schHist);
			}
		};
	}

	public Runnable reinitReport(Schedule schedule) {
		return () -> {
			Report report = schedule.getReport();
			Calendar nextDate = repHelp.getNextDate(report.getTimeRange());
			schedule.setNextDate(nextDate.getTime());
			scheduleRepo.save(schedule);
		};
	}

}
