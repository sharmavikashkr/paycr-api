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

import com.paycr.common.bean.gst.Gstr1Nil;
import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.data.domain.Invoice;
import com.paycr.report.helper.Gstr1Helper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr1NilService {

	@Autowired
	private Gstr1Helper gstHelp;

	@Async
	public Future<Boolean> collectNilList(Gstr1Report gstr1Report, List<Invoice> invoiceList) {
		List<Gstr1Nil> nilList = new ArrayList<Gstr1Nil>();
		for (Invoice invoice : invoiceList) {
			gstHelp.getSupplyBreakup(invoice, nilList);
		}
		gstr1Report.setNil(nilList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getNilCsv(List<Gstr1Nil> nilReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Supply Type", "Nil Rated", "Exemted", "Non GST" });
		Iterator<Gstr1Nil> it = nilReport.iterator();
		while (it.hasNext()) {
			Gstr1Nil nilr = it.next();
			records.add(new String[] { nilr.getSupplyType().name(), nilr.getNilRated().toString(),
					nilr.getExempted().toString(), nilr.getNonGst().toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
