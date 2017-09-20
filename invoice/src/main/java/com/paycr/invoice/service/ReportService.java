package com.paycr.invoice.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.DateFilter;
import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.bean.Server;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringReport;
import com.paycr.common.data.domain.RecurringReportUser;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.RecurringReportRepository;
import com.paycr.common.data.repository.RecurringReportUserRepository;
import com.paycr.common.data.repository.ReportRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;
import com.paycr.invoice.helper.ReportHelper;

@Service
public class ReportService {

	@Autowired
	private ReportRepository repRepo;

	@Autowired
	private RecurringReportRepository recRepRepo;

	@Autowired
	private RecurringReportUserRepository recRepUserRepo;

	@Autowired
	private ReportHelper repHelp;

	@Autowired
	private PaymentRepository payRepo;

	@Autowired
	private Company company;

	@Autowired
	private Server server;

	@Autowired
	private EmailEngine emailEngine;

	public List<Report> fetchReports(Merchant merchant) {
		List<Report> commonReports = repRepo.findByMerchant(null);
		List<Report> myReports = new ArrayList<>();
		if (merchant != null) {
			myReports = repRepo.findByMerchant(merchant);
		}
		commonReports.addAll(myReports);
		return commonReports;
	}

	public void createReports(Report report) {
		isValidReport(report);
		int exstRepSize = repRepo.findByMerchant(report.getMerchant()).size();
		if (exstRepSize >= 10) {
			throw new PaycrException(Constants.FAILURE, "Max 10 reports can be configured");
		}
		repRepo.save(report);
	}

	public String downloadReport(Report report, Merchant merchant) throws IOException {
		List<InvoiceReport> invReport = loadReport(report, merchant);
		return repHelp.getCsv(invReport);
	}

	public List<InvoiceReport> loadReport(Report report, Merchant merchant) {
		isValidReport(report);
		List<Payment> allPayments = new ArrayList<>();
		DateFilter dateFilter = repHelp.getDateFilter(report.getTimeRange());
		if (merchant == null) {
			allPayments.addAll(payRepo.findPaysWithMode(report.getPayMode(), report.getPayType(),
					dateFilter.getStartDate(), dateFilter.getEndDate()));
		} else {
			allPayments.addAll(payRepo.findPaysWithModeForMerchant(report.getPayMode(), report.getPayType(), merchant,
					dateFilter.getStartDate(), dateFilter.getEndDate()));
		}
		return repHelp.prepareReport(report, allPayments);
	}

	public void deleteReport(Integer reportId, Merchant merchant) {
		if (merchant == null) {
			throw new PaycrException(Constants.FAILURE, "Report cannot be deleted");
		}
		Report report = repRepo.findByIdAndMerchant(reportId, merchant);
		if (report == null) {
			throw new PaycrException(Constants.FAILURE, "Report not found");
		}
		repRepo.delete(reportId);
	}

	private void isValidReport(Report report) {
		if (report.getName() == null || report.getPayStatus() == null || report.getTimeRange() == null
				|| report.getPayType() == null || report.getPayMode() == null) {
			throw new PaycrException(Constants.FAILURE, "Mandatory params missing");
		}
	}

	public List<RecurringReportUser> getSchedule(PcUser user) {
		return recRepUserRepo.findByPcUser(user);
	}

	public void addSchedule(Integer reportId, Merchant merchant, PcUser user) {
		Report report = repRepo.findOne(reportId);
		if (report == null) {
			throw new PaycrException(Constants.FAILURE, "Invalid Report");
		}
		RecurringReport recRep = recRepRepo.findByReportAndMerchant(report, merchant);
		if (recRep == null) {
			recRep = new RecurringReport();
			recRep.setActive(true);
			recRep.setMerchant(merchant);
			recRep.setReport(report);
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
				Date aDayInNextQr = DateUtil.addDays(calendar.getTime(), 35);
				nextDate = DateUtil.getFirstDayOfMonth(aDayInNextQr);
			}
			recRep.setStartDate(nextDate);
			recRep.setNextDate(nextDate);
			recRepRepo.save(recRep);
		}
		int schedules = recRepUserRepo.findByPcUser(user).size();
		if (schedules >= 5) {
			throw new PaycrException(Constants.FAILURE, "Max 5 reports can be scheduled");
		}
		RecurringReportUser recRepUser = recRepUserRepo.findByRecurringReportAndPcUser(recRep, user);
		if (recRepUser != null) {
			throw new PaycrException(Constants.FAILURE, "Report already scheduled for you");
		}
		recRepUser = new RecurringReportUser();
		recRepUser.setRecurringReport(recRep);
		recRepUser.setPcUser(user);
		recRepUserRepo.save(recRepUser);
	}

	public void removeSchedule(Integer recRepUserId, PcUser user) {
		RecurringReportUser recRepUser = recRepUserRepo.findOne(recRepUserId);
		if (recRepUser == null) {
			throw new PaycrException(Constants.FAILURE, "Invalid Request");
		}
		if (recRepUser.getPcUser().getId() == user.getId()) {
			recRepUserRepo.delete(recRepUser.getId());
		} else {
			throw new PaycrException(Constants.FAILURE, "Invalid Request");
		}
	}

	public void mailReport(Report report, Merchant merchant, List<String> mailTo) throws IOException {
		String repCsv = downloadReport(report, merchant);
		DateFilter df = repHelp.getDateFilter(report.getTimeRange());
		String fileName = "";
		if (merchant != null) {
			fileName = merchant.getAccessKey() + " - " + report.getId() + ".csv";
		} else {
			fileName = "PAYCR - " + report.getId() + ".csv";
		}
		String filePath = server.getReportLocation() + fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(repCsv.getBytes());
		out.close();
		List<String> to = new ArrayList<>();
		to.addAll(mailTo);
		List<String> cc = new ArrayList<>();
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		email.setSubject("Payment Report - " + report.getName());
		email.setMessage("Payment Report - " + report.getName() + " FROM "
				+ DateUtil.getDashboardDate(df.getStartDate()) + " to " + DateUtil.getDashboardDate(df.getEndDate()));
		email.setFileName(fileName);
		email.setFilePath(filePath);
		emailEngine.sendViaGmail(email);
	}

}
