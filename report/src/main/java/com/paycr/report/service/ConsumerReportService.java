package com.paycr.report.service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.DateFilter;
import com.paycr.common.bean.report.ConsumerReport;
import com.paycr.common.data.dao.InvoiceDao;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
import com.paycr.report.helper.ReportHelper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class ConsumerReportService {

	@Autowired
	private ReportHelper repHelp;

	@Autowired
	private InvoiceDao invenseDao;

	public List<ConsumerReport> loadConsumerReport(Report report, Merchant merchant) {
		List<Object[]> dbReport = new ArrayList<>();
		DateFilter dateFilter = repHelp.getDateFilter(report.getTimeRange());
		dbReport.addAll(invenseDao.getConsumerReport(report, merchant, dateFilter));
		return prepareConReport(dbReport);
	}

	public String getConCsv(List<ConsumerReport> conReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(
				new String[] { "Name", "Email", "Mobile", "Invoices", "Refunded", "Invoice Amount", "Refund Amount" });

		Iterator<ConsumerReport> it = conReport.iterator();
		while (it.hasNext()) {
			ConsumerReport conr = it.next();
			records.add(new String[] { conr.getName(), conr.getEmail(), conr.getMobile(), conr.getInvoices().toString(),
					conr.getRefunded().toString(), conr.getInvoiceAmt().toString(), conr.getRefundAmt().toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

	public List<ConsumerReport> prepareConReport(List<Object[]> dbReport) {
		List<ConsumerReport> conReports = new ArrayList<>();
		for (Object[] dbData : dbReport) {
			ConsumerReport conReport = new ConsumerReport();
			conReport.setName((String) dbData[0]);
			conReport.setEmail((String) dbData[1]);
			conReport.setMobile((String) dbData[2]);
			conReport.setInvoices((BigInteger) dbData[3]);
			conReport.setRefunded((BigInteger) dbData[4]);
			conReport.setInvoiceAmt((Double) dbData[5]);
			conReport.setRefundAmt((Double) dbData[6]);
			conReports.add(conReport);
		}
		return conReports;
	}

}
