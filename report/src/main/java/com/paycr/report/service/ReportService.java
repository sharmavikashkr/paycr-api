package com.paycr.report.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.DateFilter;
import com.paycr.common.bean.Server;
import com.paycr.common.bean.report.AssetReport;
import com.paycr.common.bean.report.ConsumerReport;
import com.paycr.common.bean.report.ExpenseReport;
import com.paycr.common.bean.report.InventoryReport;
import com.paycr.common.bean.report.InvoiceReport;
import com.paycr.common.bean.report.SupplierReport;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringReport;
import com.paycr.common.data.domain.RecurringReportUser;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.RecurringReportRepository;
import com.paycr.common.data.repository.RecurringReportUserRepository;
import com.paycr.common.data.repository.ReportRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ReportType;
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.ReportHelper;

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
	private InvoiceReportService invRepSer;

	@Autowired
	private ExpenseReportService expRepSer;

	@Autowired
	private SupplierReportService supRepSer;

	@Autowired
	private ConsumerReportService conRepSer;

	@Autowired
	private InventoryReportService invnRepSer;

	@Autowired
	private AssetReportService astRepSer;

	@Autowired
	private Company company;

	@Autowired
	private Server server;

	@Autowired
	private EmailEngine emailEngine;

	public List<Report> fetchReports(Merchant merchant) {
		List<Report> commonReports = repRepo.findByMerchant(null);
		List<Report> myReports = new ArrayList<>();
		if (CommonUtil.isNotNull(merchant)) {
			myReports = repRepo.findByMerchant(merchant);
		}
		commonReports.addAll(myReports);
		return commonReports;
	}

	public void createReports(Report report) {
		isValidReport(report);
		int exstRepSize = repRepo.findByMerchant(report.getMerchant()).size();
		if (exstRepSize >= 10) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Max 10 reports can be configured");
		}
		repRepo.save(report);
	}

	public void deleteReport(Integer reportId, Merchant merchant) {
		if (CommonUtil.isNull(merchant)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Report cannot be deleted");
		}
		Report report = repRepo.findByIdAndMerchant(reportId, merchant);
		if (CommonUtil.isNull(report)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Report not found");
		}
		repRepo.deleteById(reportId);
	}

	private void isValidReport(Report report) {
		if (CommonUtil.isEmpty(report.getName()) || CommonUtil.isNull(report.getTimeRange())
				|| CommonUtil.isNull(report.getReportType())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Mandatory params missing");
		}
	}

	public List<RecurringReportUser> getSchedule(PcUser user) {
		return recRepUserRepo.findByPcUser(user);
	}

	public void addSchedule(Integer reportId, Merchant merchant, PcUser user) {
		Report report = repRepo.findById(reportId).get();
		if (CommonUtil.isNull(report)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Report");
		}
		RecurringReport recRep = recRepRepo.findByReportAndMerchant(report, merchant);
		if (CommonUtil.isNull(recRep)) {
			recRep = new RecurringReport();
			recRep.setActive(true);
			recRep.setMerchant(merchant);
			recRep.setReport(report);
			Date nextDateInIST = DateUtil.getUTCTimeInIST(new Date());
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
			recRep.setStartDate(calendar.getTime());
			recRep.setNextDate(calendar.getTime());
			recRepRepo.save(recRep);
		}
		int schedules = recRepUserRepo.findByPcUser(user).size();
		if (schedules >= 5) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Max 5 reports can be scheduled");
		}
		RecurringReportUser recRepUser = recRepUserRepo.findByRecurringReportAndPcUser(recRep, user);
		if (CommonUtil.isNotNull(recRepUser)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Report already scheduled for you");
		}
		recRepUser = new RecurringReportUser();
		recRepUser.setRecurringReport(recRep);
		recRepUser.setPcUser(user);
		recRepUserRepo.save(recRepUser);
	}

	public void removeSchedule(Integer recRepUserId, PcUser user) {
		RecurringReportUser recRepUser = recRepUserRepo.findById(recRepUserId).get();
		if (CommonUtil.isNull(recRepUser)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Request");
		}
		if (recRepUser.getPcUser().getId() == user.getId()) {
			recRepUserRepo.deleteById(recRepUser.getId());
		} else {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Request");
		}
	}

	public List<?> loadReport(Report report, Merchant merchant) {
		isValidReport(report);
		if (ReportType.EXPENSE.equals(report.getReportType())) {
			return expRepSer.loadExpenseReport(report, merchant);
		} else if (ReportType.INVOICE.equals(report.getReportType())) {
			return invRepSer.loadInvoiceReport(report, merchant);
		} else if (ReportType.CONSUMER.equals(report.getReportType())) {
			return conRepSer.loadConsumerReport(report, merchant);
		} else if (ReportType.SUPPLIER.equals(report.getReportType())) {
			return supRepSer.loadSupplierReport(report, merchant);
		} else if (ReportType.INVENTORY.equals(report.getReportType())) {
			return invnRepSer.loadInventoryReport(report, merchant);
		} else if (ReportType.ASSET.equals(report.getReportType())) {
			return astRepSer.loadAssetReport(report, merchant);
		}
		return null;
	}

	public String downloadReport(Report report, Merchant merchant) throws IOException {
		if (ReportType.EXPENSE.equals(report.getReportType())) {
			List<ExpenseReport> expReport = expRepSer.loadExpenseReport(report, merchant);
			return expRepSer.getExpCsv(expReport);
		} else if (ReportType.INVOICE.equals(report.getReportType())) {
			List<InvoiceReport> invReport = invRepSer.loadInvoiceReport(report, merchant);
			return invRepSer.getInvCsv(invReport);
		} else if (ReportType.CONSUMER.equals(report.getReportType())) {
			List<ConsumerReport> supReport = conRepSer.loadConsumerReport(report, merchant);
			return conRepSer.getConCsv(supReport);
		} else if (ReportType.SUPPLIER.equals(report.getReportType())) {
			List<SupplierReport> supReport = supRepSer.loadSupplierReport(report, merchant);
			return supRepSer.getSupCsv(supReport);
		} else if (ReportType.INVENTORY.equals(report.getReportType())) {
			List<InventoryReport> invnReport = invnRepSer.loadInventoryReport(report, merchant);
			return invnRepSer.getInvnCsv(invnReport);
		} else if (ReportType.ASSET.equals(report.getReportType())) {
			List<AssetReport> astReport = astRepSer.loadAssetReport(report, merchant);
			return astRepSer.getAstCsv(astReport);
		}
		return null;
	}

	@Async
	@Transactional
	public void mailReport(Report report, Merchant merchant, List<String> mailTo) throws IOException {
		String repCsv = downloadReport(report, merchant);
		DateFilter df = repHelp.getDateFilterInIST(report.getTimeRange());
		String fileName = "";
		if (CommonUtil.isNotNull(merchant)) {
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
		emailEngine.sendViaSES(email);
	}

}
