package com.paycr.dashboard.helper;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Item;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.DateUtil;

import au.com.bytecode.opencsv.CSVWriter;

@Component
public class ReportHelper {

	@Autowired
	private PaymentRepository payRepo;

	public Date getCreatedFrom(TimeRange range) {
		Calendar calendar = Calendar.getInstance();
		if (TimeRange.LAST_WEEK.equals(range)) {
			calendar.add(Calendar.DAY_OF_YEAR, -7);
		} else if (TimeRange.LAST_2WEEKS.equals(range)) {
			calendar.add(Calendar.DAY_OF_YEAR, -14);
		} else if (TimeRange.LAST_MONTH.equals(range)) {
			calendar.add(Calendar.DAY_OF_YEAR, -30);
		} else if (TimeRange.LAST_2MONTHS.equals(range)) {
			calendar.add(Calendar.DAY_OF_YEAR, -60);
		} else if (TimeRange.FOREVER.equals(range)) {
			calendar.add(Calendar.YEAR, -1);
		}
		return DateUtil.getStartOfDay(calendar.getTime());
	}

	public String getCsv(List<InvoiceReport> invReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> data = toStringArray(invReport);
		csvWriter.writeAll(data);
		csvWriter.close();
		System.out.println(writer);
		return writer.toString();
	}

	private static List<String[]> toStringArray(List<InvoiceReport> invReport) {
		List<String[]> records = new ArrayList<String[]>();
		records.add(new String[] { "Created", "Invoice Code", "Invoice Status", "Pay Amount", "Tax", "Discount",
				"Currency", "PaymentRefNo", "Pay Type", "Pay Mode", "Pay Method" });

		Iterator<InvoiceReport> it = invReport.iterator();
		while (it.hasNext()) {
			InvoiceReport invr = it.next();
			records.add(new String[] { invr.getCreated().toString(), invr.getInvoiceCode(),
					invr.getInvoiceStatus().name(), invr.getPayAmount().toString(), invr.getTax().toString(),
					invr.getDiscount().toString(), invr.getCurrency().name(), invr.getPaymentRefNo(),
					invr.getPayType().name(), invr.getPayMode().name(), invr.getPayMethod() });
		}
		return records;
	}

	@Async
	public Future<List<InvoiceReport>> prepareReport(Report report, Invoice invoice) {
		List<InvoiceReport> invoiceReports = new ArrayList<InvoiceReport>();
		List<Payment> payments = payRepo.findByInvoiceCodeAndPayType(invoice.getInvoiceCode(), report.getPayType());
		BigDecimal orgAmount = new BigDecimal(0);
		for (Item item : invoice.getItems()) {
			orgAmount = orgAmount.add(item.getPrice());
		}
		for (Payment payment : payments) {
			if (payment.getPayMode().equals(report.getPayMode()) && payment.getPayType().equals(report.getPayType())) {
				InvoiceReport invReport = new InvoiceReport();
				invReport.setCreated(invoice.getCreated());
				invReport.setInvoiceCode(invoice.getInvoiceCode());
				invReport.setInvoiceStatus(invoice.getStatus());
				invReport.setPayAmount(invoice.getPayAmount());
				invReport.setTax(invoice.getPayAmount().add(invoice.getDiscount()).subtract(orgAmount));
				invReport.setDiscount(invoice.getDiscount());
				invReport.setCurrency(invoice.getCurrency());
				invReport.setPaymentRefNo(payment.getPaymentRefNo());
				invReport.setPayType(payment.getPayType());
				invReport.setPayMode(payment.getPayMode());
				invReport.setPayMethod(payment.getMethod());
				invoiceReports.add(invReport);
			}
		}
		return new AsyncResult<List<InvoiceReport>>(invoiceReports);
	}

}
