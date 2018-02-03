package com.paycr.report.service.gst;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr1B2CNote;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.type.SupplyType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.GstHelper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr1B2CNoteService {

	@Autowired
	private GstHelper gstHelp;

	@Autowired
	private InvoiceRepository invRepo;

	public List<Gstr1B2CNote> collectB2CNoteList(List<InvoiceNote> invNoteList) {
		List<InvoiceNote> noteList = invNoteList.stream().filter(t -> (CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2CNote> b2cNoteList = new ArrayList<Gstr1B2CNote>();
		for (InvoiceNote note : noteList) {
			Invoice invoice = invRepo.findByInvoiceCode(note.getInvoiceCode());
			if (invoice.getTotalPrice().compareTo(BigDecimal.valueOf(250000)) <= 0) {
				Gstr1B2CNote b2cNote = new Gstr1B2CNote();
				b2cNote.setNoteNo(note.getNoteCode());
				b2cNote.setNoteDate(note.getCreated());
				b2cNote.setInvoiceNo(note.getInvoiceCode());
				b2cNote.setInvoiceDate(invoice.getInvoiceDate());
				b2cNote.setNoteType(note.getNoteType());
				b2cNote.setTaxableAmount(note.getTotal());
				b2cNote.setNoteAmount(note.getTotalPrice());
				b2cNote.setNoteReason(note.getNoteReason());
				List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(note.getItems());
				List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
						.collect(Collectors.toList());
				if (CommonUtil.isEmpty(igstList)) {
					b2cNote.setSupplyType(SupplyType.INTRA);
				} else {
					b2cNote.setSupplyType(SupplyType.INTER);
				}
				b2cNote.setTaxAmount(taxAmtList);
				b2cNoteList.add(b2cNote);
			}
		}
		return b2cNoteList;
	}

	public String getB2CNoteCsv(List<Gstr1B2CNote> b2cNoteReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Note No", "Note Date", "Invoice No", "Invoice Date", "Note Type", "Taxable Amount",
				"Note Amount", "Supply Type", "Note Reason", "Tax Amount" });
		Iterator<Gstr1B2CNote> it = b2cNoteReport.iterator();
		while (it.hasNext()) {
			Gstr1B2CNote b2cnr = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2cnr.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2cnr.getNoteNo(), DateUtil.getUTCTimeInISTStr(b2cnr.getNoteDate()),
					b2cnr.getInvoiceNo(), DateUtil.getUTCTimeInISTStr(b2cnr.getInvoiceDate()),
					b2cnr.getNoteType().name(), b2cnr.getTaxableAmount().toString(), b2cnr.getNoteAmount().toString(),
					b2cnr.getSupplyType().name(), b2cnr.getNoteReason(), sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
