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

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Schedule;
import com.paycr.common.data.domain.ScheduleUser;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.ScheduleRepository;
import com.paycr.common.data.repository.ScheduleUserRepository;
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

	public Runnable processReport(Schedule schedule) {
		return () -> {
			Report report = schedule.getReport();
			Merchant merchant = schedule.getMerchant();
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
			} catch (IOException ex) {
				logger.error("Execption while mailing Schedule : {} ", schedule.getId(), ex);
			}
		};
	}

}
