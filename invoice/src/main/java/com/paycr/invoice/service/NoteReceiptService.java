package com.paycr.invoice.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.bean.TaxAmount;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.PdfUtil;

@Service
public class NoteReceiptService {

	private static final Logger logger = LoggerFactory.getLogger(NoteReceiptService.class);

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
		logger.info("InvoiceNote receipt : {}", noteCode);
		Invoice invoice = invRepo.findByNoteCode(noteCode);
		InvoiceNote note = invoice.getNote();
		List<TaxAmount> taxes = new ArrayList<>();
		for (InvoiceItem item : note.getItems()) {
			List<TaxMaster> itemTaxes = new ArrayList<>();
			TaxMaster tax = item.getTax();
			List<TaxMaster> childTaxes = taxMRepo.findByParent(tax);
			if (CommonUtil.isEmpty(childTaxes)) {
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
				if (CommonUtil.isNull(taxAmt)) {
					taxAmt = new TaxAmount();
					taxAmt.setTax(itemTax);
					taxAmt.setAmount(BigDecimal.ZERO);
					taxes.add(taxAmt);
				}
				taxAmt.setAmount(taxAmt.getAmount()
						.add(item.getInventory().getRate().multiply(BigDecimal.valueOf(item.getQuantity()))
								.multiply(BigDecimal.valueOf(itemTax.getValue())).divide(BigDecimal.valueOf(100))
								.setScale(2, RoundingMode.HALF_UP)));
			}
		}
		ModelAndView mv = new ModelAndView("receipt/note");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("taxes", taxes);
		mv.addObject("invoice", invoice);
		return mv;
	}

	public File downloadPdf(String noteCode) throws IOException {
		logger.info("InvoiceNote receipt download : {}", noteCode);
		String pdfPath = server.getInvoiceLocation() + "Note-" + noteCode + ".pdf";
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()) {
			return pdfFile;
		}
		pdfFile.createNewFile();
		pdfUtil.makePdf(company.getAppUrl() + "/note/receipt/" + noteCode, pdfFile.getAbsolutePath());
		return pdfFile;
	}

}
