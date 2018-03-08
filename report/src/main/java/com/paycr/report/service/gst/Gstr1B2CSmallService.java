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

import com.paycr.common.bean.gst.Gstr1B2CSmall;
import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.type.NoteType;
import com.paycr.common.util.CommonUtil;
import com.paycr.report.helper.Gstr1Helper;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class Gstr1B2CSmallService {

	@Autowired
	private Gstr1Helper gstHelp;

	@Autowired
	private InvoiceRepository invRepo;

	@Async
	public Future<Boolean> collectB2CSmallList(Gstr1Report gstr1Report, List<Invoice> invoiceList,
			List<InvoiceNote> invNoteList) {
		List<Invoice> smallInvList = invoiceList.stream()
				.filter(t -> ((BigDecimal.valueOf(250000).compareTo(t.getTotalPrice()) >= 0)
						&& CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2CSmall> b2cSmallList = new ArrayList<Gstr1B2CSmall>();
		for (Invoice invoice : smallInvList) {
			List<Gstr1B2CSmall> taxbrkList = gstHelp.getTaxBreakup(invoice.getItems());
			for (Gstr1B2CSmall taxBrk : taxbrkList) {
				List<Gstr1B2CSmall> exstB2CSmallFt = b2cSmallList.stream()
						.filter(t -> (t.getGstRate() == taxBrk.getGstRate()
								&& taxBrk.getSupplyType().equals(t.getSupplyType())))
						.collect(Collectors.toList());
				if (CommonUtil.isEmpty(exstB2CSmallFt)) {
					b2cSmallList.add(taxBrk);
				} else {
					Gstr1B2CSmall b2cSmallInv = exstB2CSmallFt.get(0);
					b2cSmallInv.setTaxableAmount(b2cSmallInv.getTaxableAmount().add(taxBrk.getTaxableAmount()));
					b2cSmallInv.setSgstAmount(b2cSmallInv.getSgstAmount().add(taxBrk.getSgstAmount()));
					b2cSmallInv.setCgstAmount(b2cSmallInv.getCgstAmount().add(taxBrk.getCgstAmount()));
					b2cSmallInv.setIgstAmount(b2cSmallInv.getIgstAmount().add(taxBrk.getIgstAmount()));
				}
			}
		}
		List<InvoiceNote> noteList = invNoteList.stream().filter(t -> (CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		for (InvoiceNote note : noteList) {
			if (BigDecimal.valueOf(250000)
					.compareTo(invRepo.findByInvoiceCode(note.getInvoiceCode()).getTotalPrice()) >= 0) {
				List<Gstr1B2CSmall> taxbrkList = gstHelp.getTaxBreakup(note.getItems());
				for (Gstr1B2CSmall taxBrk : taxbrkList) {
					List<Gstr1B2CSmall> exstB2CSmallFt = b2cSmallList.stream()
							.filter(t -> (t.getGstRate() == taxBrk.getGstRate()
									&& taxBrk.getSupplyType().equals(t.getSupplyType())))
							.collect(Collectors.toList());
					if (NoteType.DEBIT.equals(note.getNoteType())) {
						if (CommonUtil.isEmpty(exstB2CSmallFt)) {
							b2cSmallList.add(taxBrk);
						} else {
							Gstr1B2CSmall b2cSmallInv = exstB2CSmallFt.get(0);
							b2cSmallInv.setTaxableAmount(b2cSmallInv.getTaxableAmount().add(taxBrk.getTaxableAmount()));
							b2cSmallInv.setSgstAmount(b2cSmallInv.getSgstAmount().add(taxBrk.getSgstAmount()));
							b2cSmallInv.setCgstAmount(b2cSmallInv.getCgstAmount().add(taxBrk.getCgstAmount()));
							b2cSmallInv.setIgstAmount(b2cSmallInv.getIgstAmount().add(taxBrk.getIgstAmount()));
						}
					} else {
						if (CommonUtil.isEmpty(exstB2CSmallFt)) {
							Gstr1B2CSmall creditTaxBrk = new Gstr1B2CSmall();
							creditTaxBrk.setGstRate(taxBrk.getGstRate());
							creditTaxBrk.setTaxableAmount(BigDecimal.valueOf(-1).multiply(taxBrk.getTaxableAmount()));
							creditTaxBrk.setCgstAmount(BigDecimal.valueOf(-1).multiply(taxBrk.getCgstAmount()));
							creditTaxBrk.setSgstAmount(BigDecimal.valueOf(-1).multiply(taxBrk.getSgstAmount()));
							creditTaxBrk.setIgstAmount(BigDecimal.valueOf(-1).multiply(taxBrk.getIgstAmount()));
							b2cSmallList.add(creditTaxBrk);
						} else {
							Gstr1B2CSmall b2cSmallInv = exstB2CSmallFt.get(0);
							b2cSmallInv.setTaxableAmount(
									b2cSmallInv.getTaxableAmount().subtract(taxBrk.getTaxableAmount()));
							b2cSmallInv.setSgstAmount(b2cSmallInv.getSgstAmount().subtract(taxBrk.getSgstAmount()));
							b2cSmallInv.setCgstAmount(b2cSmallInv.getCgstAmount().subtract(taxBrk.getCgstAmount()));
							b2cSmallInv.setIgstAmount(b2cSmallInv.getIgstAmount().subtract(taxBrk.getIgstAmount()));
						}
					}
				}
			}
		}
		gstr1Report.setB2cSmall(b2cSmallList);
		return new AsyncResult<Boolean>(Boolean.TRUE);
	}

	public String getB2CSmallCsv(List<Gstr1B2CSmall> b2cSmallReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Supply Type", "Gst Rate", "Taxable Amount", "CGST Amount", "SGST Amount",
				"IGST Amount" });
		Iterator<Gstr1B2CSmall> it = b2cSmallReport.iterator();
		while (it.hasNext()) {
			Gstr1B2CSmall b2bsr = it.next();
			records.add(new String[] { b2bsr.getSupplyType().name(), String.valueOf(b2bsr.getGstRate()),
					b2bsr.getTaxableAmount().toString(), b2bsr.getCgstAmount().toString(),
					b2bsr.getSgstAmount().toString(), b2bsr.getIgstAmount().toString() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

}
