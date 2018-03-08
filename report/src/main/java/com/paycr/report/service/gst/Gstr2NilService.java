package com.paycr.report.service.gst;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.gst.Gstr2Nil;
import com.paycr.common.bean.gst.Gstr2Report;
import com.paycr.common.data.domain.Expense;
import com.paycr.report.helper.Gstr2Helper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr2NilService {

	@Autowired
	private Gstr2Helper gstHelp;

	@Async
	public Future<Boolean> collectNilList(Gstr2Report gstr2Report, List<Expense> expenseList) {
		List<Gstr2Nil> nilList = new ArrayList<Gstr2Nil>();
		for (Expense expense : expenseList) {
			gstHelp.getSupplyBreakup(expense, nilList);
		}
		gstr2Report.setNil(nilList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getNilCsv(List<Gstr2Nil> nilReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Supply Type", "Nil Rated", "Exemted", "Non GST" });
		Iterator<Gstr2Nil> it = nilReport.iterator();
		while (it.hasNext()) {
			Gstr2Nil nilr = it.next();
			records.add(new String[] { nilr.getSupplyType().name(), nilr.getNilRated().toString(),
					nilr.getExempted().toString(), nilr.getNonGst().toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
