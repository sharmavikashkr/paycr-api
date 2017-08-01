package com.paycr.dashboard.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.ReportRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.dashboard.helper.ReportHelper;

@Service
public class ReportService {

	@Autowired
	private ReportRepository repRepo;

	@Autowired
	private ReportHelper repHelp;

	@Autowired
	private PaymentRepository payRepo;

	public List<Report> fetchReports(Merchant merchant) {
		List<Report> commonReports = repRepo.findByMerchant(null);
		List<Report> myReports = new ArrayList<Report>();
		if (merchant != null) {
			myReports = repRepo.findByMerchant(merchant);
		}
		commonReports.addAll(myReports);
		return commonReports;
	}

	public void createReports(Report report) {
		isValidReport(report);
		repRepo.save(report);
	}

	public String downloadReport(Report report, Merchant merchant) throws IOException {
		List<InvoiceReport> invReport = loadReport(report, merchant);
		return repHelp.getCsv(invReport);
	}

	public List<InvoiceReport> loadReport(Report report, Merchant merchant) {
		isValidReport(report);
		List<Payment> allPayments = new ArrayList<>();
		Date createdTo = new Date();
		Date createdFrom = repHelp.getCreatedFrom(report.getTimeRange());
		if (merchant == null) {
			allPayments
					.addAll(payRepo.findPaysWithMode(report.getPayMode(), report.getPayType(), createdFrom, createdTo));
		} else {
			allPayments.addAll(payRepo.findPaysWithModeForMerchant(report.getPayMode(), report.getPayType(), merchant,
					createdFrom, createdTo));
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

}
