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
import com.paycr.common.bean.InventoryReport;
import com.paycr.common.data.dao.InventoryDao;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class InventoryReportService {

	@Autowired
	private ReportHelper repHelp;

	@Autowired
	private InventoryDao invnDao;

	public List<InventoryReport> loadInventoryReport(Report report, Merchant merchant) {
		List<Object[]> dbReport = new ArrayList<>();
		DateFilter dateFilter = repHelp.getDateFilterInIST(report.getTimeRange());
		repHelp.setDateFilterInUTC(dateFilter);
		dbReport.addAll(invnDao.getInventoryReport(report, merchant, dateFilter));
		return prepareInvnReport(dbReport);
	}

	public String getInvnCsv(List<InventoryReport> invnReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Code", "Name", "Rate", "Sale Quantity", "Sale Amount" });

		Iterator<InventoryReport> it = invnReport.iterator();
		while (it.hasNext()) {
			InventoryReport invnr = it.next();
			records.add(new String[] { invnr.getCode(), invnr.getName(), invnr.getRate().toString(),
					invnr.getSaleQuantity().toString(), invnr.getSaleAmt().toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

	public List<InventoryReport> prepareInvnReport(List<Object[]> dbReport) {
		List<InventoryReport> invnReports = new ArrayList<>();
		for (Object[] dbData : dbReport) {
			InventoryReport invnReport = new InventoryReport();
			invnReport.setCode((String) dbData[0]);
			invnReport.setName((String) dbData[1]);
			invnReport.setRate((Double) dbData[2]);
			invnReport.setSaleQuantity((BigInteger) dbData[3]);
			invnReport.setSaleAmt((Double) dbData[4]);
			invnReports.add(invnReport);
		}
		return invnReports;
	}

}
