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
import com.paycr.common.bean.gst.Gstr2B2BUR;
import com.paycr.common.bean.gst.Gstr2Report;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.type.SupplyType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.Gstr2Helper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr2B2BURService {

	@Autowired
	private Gstr2Helper gstHelp;

	@Async
	public Future<Boolean> collectB2BUrList(Gstr2Report gstr2Report, List<Expense> expenseList) {
		List<Expense> expList = expenseList.stream().filter(t -> (CommonUtil.isEmpty(t.getSupplier().getGstin())))
				.collect(Collectors.toList());
		List<Gstr2B2BUR> b2bUrList = new ArrayList<Gstr2B2BUR>();
		for (Expense expense : expList) {
			Gstr2B2BUR b2bUrExp = new Gstr2B2BUR();
			b2bUrExp.setTaxableAmount(expense.getTotal());
			b2bUrExp.setInvoiceAmount(expense.getTotalPrice());
			b2bUrExp.setInvoiceDate(expense.getInvoiceDate());
			b2bUrExp.setInvoiceNo(expense.getInvoiceCode());
			if (CommonUtil.isNotNull(expense.getSupplier().getAddress())) {
				b2bUrExp.setPlaceOfSupply(expense.getSupplier().getAddress().getState());
			}
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(expense.getItems());
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isEmpty(igstList)) {
				b2bUrExp.setSupplyType(SupplyType.INTRA);
			} else {
				b2bUrExp.setSupplyType(SupplyType.INTER);
			}
			b2bUrExp.setTaxAmount(taxAmtList);
			b2bUrList.add(b2bUrExp);
		}
		gstr2Report.setB2bUr(b2bUrList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getB2BUrCsv(List<Gstr2B2BUR> b2bUrReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Invoice No", "Taxable Amount", "Invoice Amount", "Invoice Date", "Place Of Supply",
				"Supply Type", "Tax Amount" });
		Iterator<Gstr2B2BUR> it = b2bUrReport.iterator();
		while (it.hasNext()) {
			Gstr2B2BUR b2urr = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2urr.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2urr.getInvoiceNo(), b2urr.getTaxableAmount().toString(),
					b2urr.getInvoiceAmount().toString(), DateUtil.getDefaultDateTime(b2urr.getInvoiceDate()),
					b2urr.getPlaceOfSupply(), b2urr.getSupplyType().name(), sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
