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
import com.paycr.common.bean.gst.Gstr1B2B;
import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.type.SupplyType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.GstHelper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr1B2BService {

	@Autowired
	private GstHelper gstHelp;

	@Async
	public Future<Boolean> collectB2BList(Gstr1Report gstr1Report, List<Invoice> invoiceList) {
		List<Invoice> b2bInvList = invoiceList.stream().filter(t -> (!CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2B> b2bList = new ArrayList<Gstr1B2B>();
		for (Invoice invoice : b2bInvList) {
			Gstr1B2B b2bInv = new Gstr1B2B();
			b2bInv.setGstin(invoice.getConsumer().getGstin());
			b2bInv.setTaxableAmount(invoice.getTotal());
			b2bInv.setInvoiceAmount(invoice.getTotalPrice());
			b2bInv.setInvoiceDate(invoice.getInvoiceDate());
			b2bInv.setInvoiceNo(invoice.getInvoiceCode());
			if (CommonUtil.isNotNull(invoice.getConsumer().getShippingAddress())) {
				b2bInv.setPlaceOfSupply(invoice.getConsumer().getShippingAddress().getState());
			}
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(invoice.getItems());
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isEmpty(igstList)) {
				b2bInv.setSupplyType(SupplyType.INTRA);
			} else {
				b2bInv.setSupplyType(SupplyType.INTER);
			}
			b2bInv.setTaxAmount(taxAmtList);
			b2bList.add(b2bInv);
		}
		gstr1Report.setB2b(b2bList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getB2BCsv(List<Gstr1B2B> b2bReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "GSTIN", "Invoice No", "Taxable Amount", "Invoice Amount", "Invoice Date",
				"Place Of Supply", "Supply Type", "Tax Amount" });
		Iterator<Gstr1B2B> it = b2bReport.iterator();
		while (it.hasNext()) {
			Gstr1B2B b2br = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2br.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2br.getGstin(), b2br.getInvoiceNo(), b2br.getTaxableAmount().toString(),
					b2br.getInvoiceAmount().toString(), DateUtil.getUTCTimeInISTStr(b2br.getInvoiceDate()),
					b2br.getPlaceOfSupply(), b2br.getSupplyType().name(), sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
