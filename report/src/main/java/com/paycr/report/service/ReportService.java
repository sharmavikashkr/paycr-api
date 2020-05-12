package com.paycr.report.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.domain.Schedule;
import com.paycr.common.data.repository.ReportRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ReportType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.ReportHelper;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

	@Autowired
	private ReportRepository repRepo;

	@Autowired
	private ReportHelper repHelp;

	@Autowired
	private ScheduleService schSer;

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

	public List<Report> fetchReports(Merchant merchant, PcUser user) {
		List<Report> commonReports = repRepo.findByMerchant(null);
		List<Report> myReports = new ArrayList<>();
		if (CommonUtil.isNotNull(merchant)) {
			myReports = repRepo.findByMerchant(merchant);
		}
		commonReports.addAll(myReports);
		List<Schedule> Schedules = schSer.getSchedules(user);
		Schedules.forEach(rr -> {
			commonReports.forEach(r -> {
				if (r.getId() == rr.getReport().getId()) {
					r.setSchedule(rr);
				}
			});
		});
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
		if (CommonUtil.isEmpty(mailTo)) {
			return;
		}
		String repCsv = downloadReport(report, merchant);
		DateFilter df = repHelp.getDateFilter(report.getTimeRange());
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
