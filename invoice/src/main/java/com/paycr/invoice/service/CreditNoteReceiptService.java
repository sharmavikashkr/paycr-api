package com.paycr.invoice.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.bean.TaxAmount;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCreditNote;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.util.PdfUtil;

@Service
public class CreditNoteReceiptService {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Autowired
	private Server server;

	@Autowired
	private Company company;

	@Autowired
	private PdfUtil pdfUtil;

	public ModelAndView getReceiptModelAndView(String noteCode) {
		Invoice invoice = invRepo.findByCreditNoteCode(noteCode);
		InvoiceCreditNote creditNote = invoice.getCreditNote();
		List<TaxAmount> taxes = new ArrayList<>();
		for (InvoiceItem item : creditNote.getItems()) {
			List<TaxMaster> itemTaxes = new ArrayList<>();
			TaxMaster tax = item.getTax();
			List<TaxMaster> childTaxes = taxMRepo.findByParent(tax);
			if (childTaxes == null || childTaxes.isEmpty()) {
				itemTaxes.add(tax);
			} else {
				itemTaxes.addAll(childTaxes);
			}
			for (TaxMaster itemTax : itemTaxes) {
				TaxAmount taxAmt = null;
				for (TaxAmount taxA : taxes) {
					if (taxA.getTax().getId() == itemTax.getId()) {
						taxAmt = taxA;
						break;
					}
				}
				if (taxAmt == null) {
					taxAmt = new TaxAmount();
					taxAmt.setTax(itemTax);
					taxAmt.setAmount(new BigDecimal(0));
					taxes.add(taxAmt);
				}
				taxAmt.setAmount(item.getInventory().getRate().multiply(new BigDecimal(item.getQuantity()))
						.multiply(new BigDecimal(itemTax.getValue())).divide(new BigDecimal(100))
						.setScale(2, BigDecimal.ROUND_UP));
			}
		}
		ModelAndView mv = new ModelAndView("receipt/creditNote");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("taxes", taxes);
		mv.addObject("invoice", invoice);
		return mv;
	}

	public File downloadPdf(String noteCode) throws IOException {
		String pdfPath = server.getInvoiceLocation() + "CreditNote-" + noteCode + ".pdf";
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()) {
			return pdfFile;
		}
		pdfFile.createNewFile();
		pdfUtil.makePdf(company.getAppUrl() + "/creditNote/receipt/" + noteCode, pdfFile.getAbsolutePath());
		return pdfFile;
	}

}
