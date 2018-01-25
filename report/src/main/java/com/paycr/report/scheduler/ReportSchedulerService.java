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
import com.paycr.common.data.domain.RecurringReport;
import com.paycr.common.data.domain.RecurringReportUser;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.RecurringReportRepository;
import com.paycr.common.data.repository.RecurringReportUserRepository;
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.DateUtil;
import com.paycr.report.service.ReportService;

@Service
public class ReportSchedulerService {

	private static final Logger logger = LoggerFactory.getLogger(ReportSchedulerService.class);

	@Autowired
	private RecurringReportRepository recRepRepo;

	@Autowired
	private RecurringReportUserRepository recRepUserRepo;

	@Autowired
	private ReportService repSer;

	@Transactional
	public void recurrReports() {
		Date timeNow = new Date();
		Date start = DateUtil.getStartOfDay(timeNow);
		Date end = DateUtil.getEndOfDay(timeNow);
		List<RecurringReport> recRepList = recRepRepo.findTodaysRecurringReports(start, end);
		ExecutorService exec = Executors.newFixedThreadPool(5);
		for (RecurringReport recRep : recRepList) {
			exec.execute(processReport(recRep));
		}
	}

	public Runnable processReport(RecurringReport recRep) {
		return () -> {
			Report report = recRep.getReport();
			Merchant merchant = recRep.getMerchant();
			try {
				List<String> mailTo = new ArrayList<>();
				List<RecurringReportUser> recRepUsers = recRepUserRepo.findByRecurringReport(recRep);
				for (RecurringReportUser recRepUser : recRepUsers) {
					mailTo.add(recRepUser.getPcUser().getEmail());
				}
				repSer.mailReport(report, merchant, mailTo);

				Date nextDateInIST = DateUtil.getUTCTimeInIST(recRep.getNextDate());
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
				recRep.setNextDate(calendar.getTime());
				recRepRepo.save(recRep);
			} catch (IOException ex) {
				logger.error("Execption while mailing RecurringReport : {} ", recRep.getId(), ex);
			}
		};
	}

}
