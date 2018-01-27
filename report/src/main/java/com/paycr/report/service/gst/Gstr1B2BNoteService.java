package com.paycr.report.service.gst;

import java.util.ArrayList;
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
import com.paycr.report.helper.GstHelper;

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

}
