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
import com.paycr.common.bean.gst.Gstr1B2BNote;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.GstHelper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr1B2BNoteService {

	@Autowired
	private GstHelper gstHelp;

	@Autowired
	private InvoiceRepository invRepo;

	public List<Gstr1B2BNote> collectB2BNoteList(List<InvoiceNote> invNoteList) {
		List<InvoiceNote> noteList = invNoteList.stream().filter(t -> (!CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2BNote> b2bNoteList = new ArrayList<Gstr1B2BNote>();
		for (InvoiceNote note : noteList) {
			Invoice invoice = invRepo.findByInvoiceCode(note.getInvoiceCode());
			Gstr1B2BNote b2bNote = new Gstr1B2BNote();
			b2bNote.setGstin(note.getConsumer().getGstin());
			b2bNote.setNoteNo(note.getNoteCode());
			b2bNote.setNoteDate(note.getCreated());
			b2bNote.setInvoiceNo(note.getInvoiceCode());
			b2bNote.setInvoiceDate(invoice.getInvoiceDate());
			b2bNote.setNoteType(note.getNoteType());
			b2bNote.setNoteAmount(note.getTotalPrice());
			b2bNote.setNoteReason(note.getNoteReason());
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(note.getItems());
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isNull(igstList) || igstList.isEmpty()) {
				b2bNote.setSupplyType("Intra-State");
			} else {
				b2bNote.setSupplyType("Inter-State");
			}
			b2bNote.setTaxAmount(taxAmtList);
			b2bNoteList.add(b2bNote);
		}
		return b2bNoteList;
	}

	public String getB2BNoteCsv(List<Gstr1B2BNote> b2bNoteReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "GSTIN", "Note No", "Note Date", "Invoice No", "Invoice Date", "Note Type",
				"Note Amount", "Supply Type", "Note Reason", "Tax Amount" });
		Iterator<Gstr1B2BNote> it = b2bNoteReport.iterator();
		while (it.hasNext()) {
			Gstr1B2BNote b2bnr = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2bnr.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2bnr.getGstin(), b2bnr.getNoteNo(),
					DateUtil.getUTCTimeInISTStr(b2bnr.getNoteDate()), b2bnr.getInvoiceNo(),
					DateUtil.getUTCTimeInISTStr(b2bnr.getInvoiceDate()), b2bnr.getNoteType().name(),
					b2bnr.getNoteAmount().toString(), b2bnr.getSupplyType(), b2bnr.getNoteReason(), sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
