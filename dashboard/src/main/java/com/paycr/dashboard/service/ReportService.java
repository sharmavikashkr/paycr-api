package com.paycr.dashboard.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.data.dao.InvoiceDao;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.ReportRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.dashboard.helper.ReportHelper;

@Service
public class ReportService {

	@Autowired
	private InvoiceDao invDao;

	@Autowired
	private ReportRepository repRepo;

	@Autowired
	private ReportHelper repHelp;

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
		SearchInvoiceRequest searchReq = new SearchInvoiceRequest();
		Date createdTo = new Date();
		Date createdFrom = repHelp.getCreatedFrom(report.getTimeRange());
		searchReq.setCreatedFrom(createdFrom);
		searchReq.setCreatedTo(createdTo);
		searchReq.setInvoiceStatus(report.getInvoiceStatus());
		List<Invoice> invoices = invDao.findAllInvoices(searchReq, merchant);
		List<InvoiceReport> invoiceReports = new ArrayList<InvoiceReport>();
		List<Future<List<InvoiceReport>>> dataListFutures = new ArrayList<Future<List<InvoiceReport>>>();
		for (Invoice invoice : invoices) {
			dataListFutures.add(repHelp.prepareReport(report, invoice));
		}
		for (Future<List<InvoiceReport>> dataListFuture : dataListFutures) {
			while (!dataListFuture.isDone() && !dataListFuture.isCancelled()) {
			}
			try {
				invoiceReports.addAll(dataListFuture.get());
			} catch (Exception ex) {
				throw new PaycrException(Constants.FAILURE, "Processing failed");
			}
		}
		return invoiceReports;
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
		if (report.getName() == null || report.getInvoiceStatus() == null || report.getTimeRange() == null
				|| report.getPayType() == null || report.getPayMode() == null) {
			throw new PaycrException(Constants.FAILURE, "Mandatory params missing");
		}
	}

}
