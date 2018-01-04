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
import com.paycr.common.bean.SupplierReport;
import com.paycr.common.data.dao.ExpenseDao;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class SupplierReportService {

	@Autowired
	private ReportHelper repHelp;

	@Autowired
	private ExpenseDao expenseDao;

	public List<SupplierReport> loadSupplierReport(Report report, Merchant merchant) {
		List<Object[]> dbReport = new ArrayList<>();
		DateFilter dateFilter = repHelp.getDateFilterInIST(report.getTimeRange());
		repHelp.setDateFilterInUTC(dateFilter);
		dbReport.addAll(expenseDao.getSupplierReport(report, merchant, dateFilter));
		return prepareSupReport(dbReport);
	}

	public String getSupCsv(List<SupplierReport> supReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(
				new String[] { "Name", "Email", "Mobile", "Expenses", "Refunded", "Expense Amount", "Refund Amount" });

		Iterator<SupplierReport> it = supReport.iterator();
		while (it.hasNext()) {
			SupplierReport supr = it.next();
			records.add(new String[] { supr.getName(), supr.getEmail(), supr.getMobile(), supr.getExpenses().toString(),
					supr.getRefunded().toString(), supr.getExpenseAmt().toString(), supr.getRefundAmt().toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

	public List<SupplierReport> prepareSupReport(List<Object[]> dbReport) {
		List<SupplierReport> supReports = new ArrayList<>();
		for (Object[] dbData : dbReport) {
			SupplierReport supReport = new SupplierReport();
			supReport.setName((String) dbData[0]);
			supReport.setEmail((String) dbData[1]);
			supReport.setMobile((String) dbData[2]);
			supReport.setExpenses((BigInteger) dbData[3]);
			supReport.setRefunded((BigInteger) dbData[4]);
			supReport.setExpenseAmt((Double) dbData[5]);
			supReport.setRefundAmt((Double) dbData[6]);
			supReports.add(supReport);
		}
		return supReports;
	}

}
