package com.paycr.report.scheduler;

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
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.DateUtil;
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

				Date nextDateInIST = DateUtil.getUTCTimeInIST(schedule.getNextDate());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(nextDateInIST);
				if (TimeRange.YESTERDAY.equals(report.getTimeRange())) {
					Date aTimeTomorrow = DateUtil.addDays(calendar.getTime(), 1);
					nextDateInIST = DateUtil.getStartOfDay(aTimeTomorrow);
				} else if (TimeRange.LAST_WEEK.equals(report.getTimeRange())) {
					Date aDayInNextWeek = DateUtil.addDays(calendar.getTime(), 7);
					nextDateInIST = DateUtil.getFirstDayOfWeek(aDayInNextWeek);
				} else if (TimeRange.LAST_MONTH.equals(report.getTimeRange())) {
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
					Date aDayInNextMonth = DateUtil.addDays(calendar.getTime(), 35);
					nextDateInIST = DateUtil.getFirstDayOfMonth(aDayInNextMonth);
				} else if (TimeRange.LAST_YEAR.equals(report.getTimeRange())) {
					calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
					Date aDayInNextYear = DateUtil.addDays(calendar.getTime(), 100);
					nextDateInIST = DateUtil.getFirstDayOfYear(aDayInNextYear);
				}
				calendar.setTime(DateUtil.getISTTimeInUTC(nextDateInIST));
				calendar.set(Calendar.HOUR_OF_DAY, 20);
				calendar.set(Calendar.MINUTE, 0);
				schedule.setNextDate(calendar.getTime());
				scheduleRepo.save(schedule);
			} catch (IOException ex) {
				logger.error("Execption while mailing Schedule : {} ", schedule.getId(), ex);
			}
		};
	}

}
