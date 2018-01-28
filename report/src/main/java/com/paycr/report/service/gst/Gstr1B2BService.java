package com.paycr.report.service.gst;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr1B2B;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.util.CommonUtil;
import com.paycr.report.helper.GstHelper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr1B2BService {

	@Autowired
	private GstHelper gstHelp;

	public List<Gstr1B2B> collectB2BList(List<Invoice> invoiceList) {
		List<Invoice> b2bInvList = invoiceList.stream().filter(t -> (!CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2B> b2bList = new ArrayList<Gstr1B2B>();
		for (Invoice invoice : b2bInvList) {
			Gstr1B2B b2bInv = new Gstr1B2B();
			b2bInv.setGstin(invoice.getConsumer().getGstin());
			b2bInv.setInvoiceAmount(invoice.getTotalPrice());
			b2bInv.setInvoiceDate(invoice.getInvoiceDate());
			b2bInv.setInvoiceNo(invoice.getInvoiceCode());
			if (CommonUtil.isNotNull(invoice.getConsumer().getShippingAddress())) {
				b2bInv.setPlaceOfSupply(invoice.getConsumer().getShippingAddress().getState());
			}
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(invoice.getItems());
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isNull(igstList) || igstList.isEmpty()) {
				b2bInv.setSupplyType("Intra-State");
			} else {
				b2bInv.setSupplyType("Inter-State");
			}
			b2bInv.setTaxAmount(taxAmtList);
			b2bList.add(b2bInv);
		}
		return b2bList;
	}

	public String getB2BCsv(List<Gstr1B2B> b2bReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "GSTIN", "Invoice No", "Invoice Amount", "Invoice Date", "Place Of Supply",
				"Supply Type", "Tax Amount" });
		Iterator<Gstr1B2B> it = b2bReport.iterator();
		while (it.hasNext()) {
			Gstr1B2B b2br = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2br.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2br.getGstin(), b2br.getInvoiceNo(), b2br.getInvoiceAmount().toString(),
					b2br.getInvoiceDate().toString(), b2br.getPlaceOfSupply(), b2br.getSupplyType(), sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
