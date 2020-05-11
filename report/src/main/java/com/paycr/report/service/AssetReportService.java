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
import com.paycr.common.bean.report.AssetReport;
import com.paycr.common.data.dao.AssetDao;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
import com.paycr.report.helper.ReportHelper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class AssetReportService {

	@Autowired
	private ReportHelper repHelp;

	@Autowired
	private AssetDao astDao;

	public List<AssetReport> loadAssetReport(Report report, Merchant merchant) {
		List<Object[]> dbReport = new ArrayList<>();
		DateFilter dateFilter = repHelp.getDateFilter(report.getTimeRange());
		dbReport.addAll(astDao.getAssetReport(report, merchant, dateFilter));
		return prepareAstReport(dbReport);
	}

	public String getAstCsv(List<AssetReport> astReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Code", "Name", "Rate", "Sale Quantity", "Sale Amount" });

		Iterator<AssetReport> it = astReport.iterator();
		while (it.hasNext()) {
			AssetReport astr = it.next();
			records.add(new String[] { astr.getCode(), astr.getName(), astr.getRate().toString(),
					astr.getSaleQuantity().toString(), astr.getSaleAmt().toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

	public List<AssetReport> prepareAstReport(List<Object[]> dbReport) {
		List<AssetReport> astReports = new ArrayList<>();
		for (Object[] dbData : dbReport) {
			AssetReport astReport = new AssetReport();
			astReport.setCode((String) dbData[0]);
			astReport.setName((String) dbData[1]);
			astReport.setRate((Double) dbData[2]);
			astReport.setSaleQuantity((BigInteger) dbData[3]);
			astReport.setSaleAmt((Double) dbData[4]);
			astReports.add(astReport);
		}
		return astReports;
	}

}
