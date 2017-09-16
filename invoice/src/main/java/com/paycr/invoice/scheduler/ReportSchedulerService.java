package com.paycr.invoice.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.DateFilter;
import com.paycr.common.bean.Server;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.RecurringReport;
import com.paycr.common.data.domain.RecurringReportUser;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.RecurringReportRepository;
import com.paycr.common.data.repository.RecurringReportUserRepository;
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.DateUtil;
import com.paycr.invoice.service.ReportService;

@Service
public class ReportSchedulerService {

	@Autowired
	private RecurringReportRepository recRepRepo;

	@Autowired
	private RecurringReportUserRepository recRepUserRepo;

	@Autowired
	private ReportService repSer;

	@Autowired
	private Company company;

	@Autowired
	private Server server;

	@Autowired
	private EmailEngine emailEngine;

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
			String repCsv = null;
			try {
				DateFilter df = repSer.getDateFilter(report.getTimeRange());
				repCsv = repSer.downloadReport(report, merchant);
				String fileName = "";
				if (merchant != null) {
					fileName = merchant.getAccessKey() + " - " + report.getId() + ".csv";
				} else {
					fileName = "PAYCR - " + report.getId() + ".csv";
				}
				String filePath = server.getReportLocation() + fileName;
				File file = new File(filePath);
				if(!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(file);
				out.write(repCsv.getBytes());
				out.close();
				List<String> to = new ArrayList<>();
				List<RecurringReportUser> recRepUsers = recRepUserRepo.findByRecurringReport(recRep);
				for (RecurringReportUser recRepUser : recRepUsers) {
					to.add(recRepUser.getPcUser().getEmail());
				}
				List<String> cc = new ArrayList<>();
				Email email = new Email(company.getContactName(), company.getContactEmail(),
						company.getContactPassword(), to, cc);
				email.setSubject("Payment Report - " + report.getName());
				email.setMessage(
						"Payment Report - " + report.getName() + " FROM " + DateUtil.getDashboardDate(df.getStartDate())
								+ " to " + DateUtil.getDashboardDate(df.getEndDate()));
				email.setFileName(fileName);
				email.setFilePath(filePath);
				emailEngine.sendViaGmail(email);

				Date nextDate = new Date();
				Calendar calendar = Calendar.getInstance();
				if (TimeRange.LAST_WEEK.equals(report.getTimeRange())) {
					Date aDayInNextWeek = DateUtil.addDays(calendar.getTime(), 7);
					nextDate = DateUtil.getFirstDayOfWeek(aDayInNextWeek);
				} else if (TimeRange.LAST_MONTH.equals(report.getTimeRange())) {
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
					Date aDayInNextMonth = DateUtil.addDays(calendar.getTime(), 35);
					nextDate = DateUtil.getFirstDayOfMonth(aDayInNextMonth);
				} else if (TimeRange.LAST_QUARTER.equals(report.getTimeRange())) {
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
					Date aDayInNextQr = DateUtil.addDays(calendar.getTime(), 100);
					nextDate = DateUtil.getFirstDayOfMonth(aDayInNextQr);
				}
				recRep.setNextDate(nextDate);
				recRepRepo.save(recRep);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
	}

}
