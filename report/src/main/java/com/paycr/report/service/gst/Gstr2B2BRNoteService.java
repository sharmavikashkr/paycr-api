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
import com.paycr.common.bean.gst.Gstr2B2BRNote;
import com.paycr.common.bean.gst.Gstr2Report;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpenseNote;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.type.SupplyType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.report.helper.Gstr2Helper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr2B2BRNoteService {

	@Autowired
	private Gstr2Helper gstHelp;

	@Autowired
	private ExpenseRepository expRepo;

	@Async
	public Future<Boolean> collectB2BRNoteList(Gstr2Report gstr2Report, List<ExpenseNote> expNoteList) {
		List<ExpenseNote> noteList = expNoteList.stream().filter(t -> (!CommonUtil.isEmpty(t.getSupplier().getGstin())))
				.collect(Collectors.toList());
		List<Gstr2B2BRNote> b2bNoteList = new ArrayList<Gstr2B2BRNote>();
		for (ExpenseNote note : noteList) {
			Expense expense = expRepo.findByExpenseCode(note.getExpenseCode());
			Gstr2B2BRNote b2bNote = new Gstr2B2BRNote();
			b2bNote.setGstin(note.getSupplier().getGstin());
			b2bNote.setNoteNo(note.getNoteCode());
			b2bNote.setNoteDate(note.getCreated());
			b2bNote.setInvoiceNo(expense.getInvoiceCode());
			b2bNote.setInvoiceDate(expense.getInvoiceDate());
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
		gstr2Report.setB2bRNote(b2bNoteList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getB2BRNoteCsv(List<Gstr2B2BRNote> b2bRNoteReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "GSTIN", "Note No", "Note Date", "Invoice No", "Invoice Date", "Note Type",
				"Taxable Amount", "Note Amount", "Supply Type", "Note Reason", "Tax Amount" });
		Iterator<Gstr2B2BRNote> it = b2bRNoteReport.iterator();
		while (it.hasNext()) {
			Gstr2B2BRNote b2brnr = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2brnr.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2brnr.getGstin(), b2brnr.getNoteNo(),
					DateUtil.getDefaultDateTime(b2brnr.getNoteDate()), b2brnr.getInvoiceNo(),
					DateUtil.getDefaultDateTime(b2brnr.getInvoiceDate()), b2brnr.getNoteType().name(),
					b2brnr.getTaxableAmount().toString(), b2brnr.getNoteAmount().toString(),
					b2brnr.getSupplyType().name(), b2brnr.getNoteReason(), sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
