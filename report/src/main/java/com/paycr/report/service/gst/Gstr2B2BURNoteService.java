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
import com.paycr.common.bean.gst.Gstr2B2BURNote;
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
public class Gstr2B2BURNoteService {

	@Autowired
	private Gstr2Helper gstHelp;

	@Autowired
	private ExpenseRepository expRepo;

	@Async
	public Future<Boolean> collectB2BUrNoteList(Gstr2Report gstr2Report, List<ExpenseNote> expNoteList) {
		List<ExpenseNote> noteList = expNoteList.stream().filter(t -> (CommonUtil.isEmpty(t.getSupplier().getGstin())))
				.collect(Collectors.toList());
		List<Gstr2B2BURNote> b2bUrNoteList = new ArrayList<Gstr2B2BURNote>();
		for (ExpenseNote note : noteList) {
			Expense expense = expRepo.findByExpenseCode(note.getExpenseCode());
			Gstr2B2BURNote b2bUrNote = new Gstr2B2BURNote();
			b2bUrNote.setNoteNo(note.getNoteCode());
			b2bUrNote.setNoteDate(note.getCreated());
			b2bUrNote.setInvoiceNo(expense.getInvoiceCode());
			b2bUrNote.setInvoiceDate(expense.getInvoiceDate());
			b2bUrNote.setNoteType(note.getNoteType());
			b2bUrNote.setTaxableAmount(note.getTotal());
			b2bUrNote.setNoteAmount(note.getTotalPrice());
			b2bUrNote.setNoteReason(note.getNoteReason());
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(note.getItems());
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isEmpty(igstList)) {
				b2bUrNote.setSupplyType(SupplyType.INTRA);
			} else {
				b2bUrNote.setSupplyType(SupplyType.INTER);
			}
			b2bUrNote.setTaxAmount(taxAmtList);
			b2bUrNoteList.add(b2bUrNote);
		}
		gstr2Report.setB2bUrNote(b2bUrNoteList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getB2BUrNoteCsv(List<Gstr2B2BURNote> b2bUrNoteReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Note No", "Note Date", "Invoice No", "Invoice Date", "Note Type", "Taxable Amount",
				"Note Amount", "Supply Type", "Note Reason", "Tax Amount" });
		Iterator<Gstr2B2BURNote> it = b2bUrNoteReport.iterator();
		while (it.hasNext()) {
			Gstr2B2BURNote b2burnr = it.next();
			StringBuilder sb = new StringBuilder();
			for (TaxAmount taxAmt : b2burnr.getTaxAmount()) {
				sb.append(taxAmt.getTax().getName() + " " + taxAmt.getTax().getValue() + " : " + taxAmt.getAmount()
						+ ",");
			}
			records.add(new String[] { b2burnr.getNoteNo(), DateUtil.getDefaultDateTime(b2burnr.getNoteDate()),
					b2burnr.getInvoiceNo(), DateUtil.getDefaultDateTime(b2burnr.getInvoiceDate()),
					b2burnr.getNoteType().name(), b2burnr.getTaxableAmount().toString(),
					b2burnr.getNoteAmount().toString(), b2burnr.getSupplyType().name(), b2burnr.getNoteReason(),
					sb.toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
