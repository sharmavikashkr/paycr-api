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
import com.paycr.common.bean.gst.Gstr1B2BNote;
import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.type.SupplyType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.Gstr1Helper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr1B2BNoteService {

	@Autowired
	private Gstr1Helper gstHelp;

	@Autowired
	private InvoiceRepository invRepo;

	@Async
	public Future<Boolean> collectB2BNoteList(Gstr1Report gstr1Report, List<InvoiceNote> invNoteList) {
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
			b2bNote.setTaxableAmount(note.getTotal());
			b2bNote.setNoteAmount(note.getTotalPrice());
			b2bNote.setNoteReason(note.getNoteReason());
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(note.getItems());
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isEmpty(igstList)) {
				b2bNote.setSupplyType(SupplyType.INTRA);
			} else {
				b2bNote.setSupplyType(SupplyType.INTER);
			}
			b2bNote.setTaxAmount(taxAmtList);
			b2bNoteList.add(b2bNote);
		}
		gstr1Report.setB2bNote(b2bNoteList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getB2BNoteCsv(List<Gstr1B2BNote> b2bNoteReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "GSTIN", "Note No", "Note Date", "Invoice No", "Invoice Date", "Note Type",
				"Taxable Amount", "Note Amount", "Supply Type", "Note Reason", "Tax Amount" });
		Iterator<Gstr1B2BNote> it = b2bNoteReport.iterator();
		while (it.hasNext()) {
			Gstr1B2BNote b2bnr = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2bnr.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2bnr.getGstin(), b2bnr.getNoteNo(),
					DateUtil.getDefaultDateTime(b2bnr.getNoteDate()), b2bnr.getInvoiceNo(),
					DateUtil.getDefaultDateTime(b2bnr.getInvoiceDate()), b2bnr.getNoteType().name(),
					b2bnr.getTaxableAmount().toString(), b2bnr.getNoteAmount().toString(), b2bnr.getSupplyType().name(),
					b2bnr.getNoteReason(), sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
