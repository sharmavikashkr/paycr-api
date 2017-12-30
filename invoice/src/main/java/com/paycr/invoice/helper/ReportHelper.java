package com.paycr.invoice.helper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paycr.common.bean.DateFilter;
import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.type.PayStatus;
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.DateUtil;

import au.com.bytecode.opencsv.CSVWriter;

@Component
public class ReportHelper {

	@Autowired
	private InvoiceRepository invRepo;

	public DateFilter getDateFilter(TimeRange range) {
		DateFilter dateFilter = null;
		Calendar calendar = Calendar.getInstance();
		if (TimeRange.LAST_WEEK.equals(range)) {
			Date aDayInLastWeek = DateUtil.addDays(calendar.getTime(), -7);
			Date start = DateUtil.getFirstDayOfWeek(aDayInLastWeek);
			Date end = DateUtil.getLastDayOfWeek(aDayInLastWeek);
			dateFilter = new DateFilter(start, end);
		} else if (TimeRange.LAST_MONTH.equals(range)) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			Date aDayInLastMonth = DateUtil.addDays(calendar.getTime(), -20);
			Date start = DateUtil.getFirstDayOfMonth(aDayInLastMonth);
			Date end = DateUtil.getLastDayOfMonth(aDayInLastMonth);
			dateFilter = new DateFilter(start, end);
		} else if (TimeRange.LAST_QUARTER.equals(range)) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			Date aDayIn3LastMonth = DateUtil.addDays(calendar.getTime(), -70);
			Date aDayInLastMonth = DateUtil.addDays(calendar.getTime(), -20);
			Date start = DateUtil.getFirstDayOfMonth(aDayIn3LastMonth);
			Date end = DateUtil.getLastDayOfMonth(aDayInLastMonth);
			dateFilter = new DateFilter(start, end);
		}
		return dateFilter;
	}

	public String getCsv(List<InvoiceReport> invReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> data = toStringArray(invReport);
		csvWriter.writeAll(data);
		csvWriter.close();
		return writer.toString();
	}

	private static List<String[]> toStringArray(List<InvoiceReport> invReport) {
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Created", "Invoice Code", "Invoice Status", "Invoice Amount", "Tax", "Discount",
				"Amount", "Currency", "PaymentRefNo", "Pay Type", "Pay Mode", "Pay Method", "Pay Status" });

		Iterator<InvoiceReport> it = invReport.iterator();
		while (it.hasNext()) {
			InvoiceReport invr = it.next();
			records.add(new String[] { invr.getCreated().toString(), invr.getInvoiceCode(),
					invr.getInvoiceStatus().name(), invr.getPayAmount().toString(), invr.getTax().toString(),
					invr.getDiscount().toString(), invr.getAmount().toString(), invr.getCurrency().name(),
					invr.getPaymentRefNo(), invr.getPayType().name(), invr.getPayMode().name(), invr.getPayMethod(),
					invr.getPayStatus() });
		}
		return records;
	}

	public List<InvoiceReport> prepareReport(Report report, List<InvoicePayment> payments) {
		List<InvoiceReport> invoiceReports = new ArrayList<>();
		for (InvoicePayment payment : payments) {
			if (include(report.getPayStatus(), payment.getStatus())) {
				Invoice invoice = invRepo.findByInvoiceCode(payment.getInvoiceCode());
				InvoiceReport invReport = new InvoiceReport();
				invReport.setCreated(payment.getCreated());
				invReport.setInvoiceCode(invoice.getInvoiceCode());
				invReport.setInvoiceStatus(invoice.getStatus());
				invReport.setPayAmount(invoice.getPayAmount());
				invReport.setAmount(payment.getAmount());
				invReport.setTax(invoice.getPayAmount().add(invoice.getDiscount()).subtract(invoice.getTotal()));
				invReport.setDiscount(invoice.getDiscount());
				invReport.setCurrency(invoice.getCurrency());
				invReport.setPaymentRefNo(payment.getPaymentRefNo());
				invReport.setPayType(payment.getPayType());
				invReport.setPayMode(payment.getPayMode());
				invReport.setPayMethod(payment.getMethod());
				invReport.setPayStatus(payment.getStatus());
				invoiceReports.add(invReport);
			}
		}
		return invoiceReports;
	}

	private boolean include(PayStatus payStatus, String status) {
		return (PayStatus.SUCCESS.equals(payStatus) && ("captured".equals(status) || "refund".equals(status)))
				|| (PayStatus.FAILURE.equals(payStatus) && !"captured".equals(status) && !"refund".equals(status));
	}

}
