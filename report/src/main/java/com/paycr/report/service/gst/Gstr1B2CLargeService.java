package com.paycr.report.service.gst;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
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
import com.paycr.common.bean.gst.Gstr1B2CLarge;
import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.type.SupplyType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.Gstr1Helper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr1B2CLargeService {

	@Autowired
	private Gstr1Helper gstHelp;

	@Async
	public Future<Boolean> collectB2CLargeList(Gstr1Report gstr1Report, List<Invoice> invoiceList) {
		List<Invoice> largeInvList = invoiceList.stream()
				.filter(t -> ((BigDecimal.valueOf(250000).compareTo(t.getTotalPrice()) < 0)
						&& CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2CLarge> b2cLargeList = new ArrayList<Gstr1B2CLarge>();
		for (Invoice invoice : largeInvList) {
			Gstr1B2CLarge b2cLargeInv = new Gstr1B2CLarge();
			b2cLargeInv.setTaxableAmount(invoice.getTotal());
			b2cLargeInv.setInvoiceAmount(invoice.getTotalPrice());
			b2cLargeInv.setInvoiceDate(invoice.getInvoiceDate());
			b2cLargeInv.setInvoiceNo(invoice.getInvoiceCode());
			if (CommonUtil.isNotNull(invoice.getConsumer().getShippingAddress())) {
				b2cLargeInv.setPlaceOfSupply(invoice.getConsumer().getShippingAddress().getState());
			}
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(invoice.getItems());
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isEmpty(igstList)) {
				b2cLargeInv.setSupplyType(SupplyType.INTRA);
			} else {
				b2cLargeInv.setSupplyType(SupplyType.INTER);
			}
			b2cLargeInv.setTaxAmount(taxAmtList);
			b2cLargeList.add(b2cLargeInv);
		}
		gstr1Report.setB2cLarge(b2cLargeList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getB2CLargeCsv(List<Gstr1B2CLarge> b2cLargeReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Invoice No", "Taxable Amount", "Invoice Amount", "Invoice Date", "Place Of Supply",
				"Supply Type", "Tax Amount" });
		Iterator<Gstr1B2CLarge> it = b2cLargeReport.iterator();
		while (it.hasNext()) {
			Gstr1B2CLarge b2clr = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2clr.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2clr.getInvoiceNo(), b2clr.getTaxableAmount().toString(),
					b2clr.getInvoiceAmount().toString(), DateUtil.getDefaultDateTime(b2clr.getInvoiceDate()),
					b2clr.getPlaceOfSupply(), b2clr.getSupplyType().name(), sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
