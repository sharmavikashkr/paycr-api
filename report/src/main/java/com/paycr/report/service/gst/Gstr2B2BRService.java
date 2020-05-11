package com.paycr.report.service.gst;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr2B2BR;
import com.paycr.common.bean.gst.Gstr2Report;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.type.SupplyType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.Gstr2Helper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr2B2BRService {

	@Autowired
	private Gstr2Helper gstHelp;

	@Async
	public Future<Boolean> collectB2BRList(Gstr2Report gstr2Report, List<Expense> expenseList) {
		List<Expense> b2bInvList = expenseList.stream().filter(t -> (!CommonUtil.isEmpty(t.getSupplier().getGstin())))
				.collect(Collectors.toList());
		List<Gstr2B2BR> b2bRList = new ArrayList<Gstr2B2BR>();
		for (Expense expensee : b2bInvList) {
			Gstr2B2BR b2bR = new Gstr2B2BR();
			b2bR.setGstin(expensee.getSupplier().getGstin());
			b2bR.setTaxableAmount(expensee.getTotal());
			b2bR.setInvoiceAmount(expensee.getTotalPrice());
			b2bR.setInvoiceDate(expensee.getInvoiceDate());
			b2bR.setInvoiceNo(expensee.getInvoiceCode());
			if (CommonUtil.isNotNull(expensee.getSupplier().getAddress())) {
				b2bR.setPlaceOfSupply(expensee.getSupplier().getAddress().getState());
			}
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(expensee.getItems());
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isEmpty(igstList)) {
				b2bR.setSupplyType(SupplyType.INTRA);
			} else {
				b2bR.setSupplyType(SupplyType.INTER);
			}
			b2bR.setTaxAmount(taxAmtList);
			b2bRList.add(b2bR);
		}
		gstr2Report.setB2bR(b2bRList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getB2BRCsv(List<Gstr2B2BR> b2bRReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "GSTIN", "Expense No", "Taxable Amount", "Expense Amount", "Expense Date",
				"Place Of Supply", "Supply Type", "Tax Amount" });
		Iterator<Gstr2B2BR> it = b2bRReport.iterator();
		while (it.hasNext()) {
			Gstr2B2BR b2br = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2br.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2br.getGstin(), b2br.getInvoiceNo(), b2br.getTaxableAmount().toString(),
					b2br.getInvoiceAmount().toString(), DateUtil.getDefaultDateTime(b2br.getInvoiceDate()),
					b2br.getPlaceOfSupply(), b2br.getSupplyType().name(), sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
