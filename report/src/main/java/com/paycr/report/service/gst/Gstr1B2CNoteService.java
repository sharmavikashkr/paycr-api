package com.paycr.report.service.gst;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr1B2CNote;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.util.CommonUtil;
import com.paycr.report.helper.GstHelper;

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
				b2cNote.setNoteAmount(note.getTotalPrice());
				b2cNote.setNoteReason(note.getNoteReason());
				List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(note.getItems());
				List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
						.collect(Collectors.toList());
				if (CommonUtil.isNull(igstList) || igstList.isEmpty()) {
					b2cNote.setSupplyType("Intra-State");
				} else {
					b2cNote.setSupplyType("Inter-State");
				}
				b2cNote.setTaxAmount(taxAmtList);
				b2cNoteList.add(b2cNote);
			}
		}
		return b2cNoteList;
	}

}
