package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.data.dao.InvoiceDao;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.ReportRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.Constants;

@Service
public class ReportService {

	@Autowired
	private InvoiceDao invDao;

	@Autowired
	private ReportRepository repRepo;

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
		repRepo.save(report);
	}

	public List<InvoiceReport> loadReport(Report report, Merchant merchant) {
		SearchInvoiceRequest searchReq = new SearchInvoiceRequest();
		Date createdTo = new Date();
		Date createdFrom = new Date();
		Calendar calendar = Calendar.getInstance();
		if (TimeRange.LAST_WEEK.equals(report.getTimeRange())) {
			calendar.add(Calendar.DAY_OF_YEAR, -7);
		} else if (TimeRange.LAST_2WEEKS.equals(report.getTimeRange())) {
			calendar.add(Calendar.DAY_OF_YEAR, -14);
		} else if (TimeRange.LAST_MONTH.equals(report.getTimeRange())) {
			calendar.add(Calendar.DAY_OF_YEAR, -30);
		} else if (TimeRange.LAST_2MONTHS.equals(report.getTimeRange())) {
			calendar.add(Calendar.DAY_OF_YEAR, -60);
		} else if (TimeRange.FOREVER.equals(report.getTimeRange())) {
			calendar.add(Calendar.YEAR, -1);
		}
		createdFrom = calendar.getTime();
		searchReq.setCreatedFrom(createdFrom);
		searchReq.setCreatedTo(createdTo);
		searchReq.setInvoiceStatus(report.getInvoiceStatus());
		List<Invoice> invoices = invDao.findAllInvoices(searchReq, merchant);
		List<InvoiceReport> invoiceReports = new ArrayList<InvoiceReport>();
		for (Invoice invoice : invoices) {
			List<Payment> payments = payRepo.findByInvoiceCodeAndPayType(invoice.getInvoiceCode(), report.getPayType());
			for (Payment payment : payments) {
				if (payment.getPayMode().equals(report.getPayMode())
						&& payment.getPayType().equals(report.getPayType())) {
					InvoiceReport invReport = new InvoiceReport();
					invReport.setCreated(invoice.getCreated());
					invReport.setInvoiceCode(invoice.getInvoiceCode());
					invReport.setInvoiceType(invoice.getInvoiceType());
					invReport.setInvoiceStatus(invoice.getStatus());
					invReport.setPayAmount(invoice.getPayAmount());
					invReport.setCurrency(invoice.getCurrency());
					invReport.setPaymentRefNo(payment.getPaymentRefNo());
					invReport.setPayType(payment.getPayType());
					invReport.setPayMode(payment.getPayMode());
					invReport.setPayMethod(payment.getMethod());
					invoiceReports.add(invReport);
				}
			}
		}
		return invoiceReports;
	}

	public void deleteReport(Integer reportId, Merchant merchant) {
		if(merchant == null) {
			throw new PaycrException(Constants.FAILURE, "Report cannot be deleted");
		}
		Report report = repRepo.findByIdAndMerchant(reportId, merchant);
		if(report == null) {
			throw new PaycrException(Constants.FAILURE, "Report not found");
		}
		repRepo.delete(reportId);
	}

}
