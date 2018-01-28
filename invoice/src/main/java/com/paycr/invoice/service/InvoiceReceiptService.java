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
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.util.PdfUtil;

@Service
public class InvoiceReceiptService {

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

	public ModelAndView getReceiptModelAndView(String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		List<TaxAmount> taxes = new ArrayList<>();
		for (InvoiceItem item : invoice.getItems()) {
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
					taxAmt.setAmount(BigDecimal.ZERO);
					taxes.add(taxAmt);
				}
				taxAmt.setAmount(taxAmt.getAmount()
						.add(item.getInventory().getRate().multiply(BigDecimal.valueOf(item.getQuantity()))
								.multiply(BigDecimal.valueOf(itemTax.getValue())).divide(BigDecimal.valueOf(100))
								.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}
		}
		ModelAndView mv = new ModelAndView("receipt/invoice");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("taxes", taxes);
		mv.addObject("invoice", invoice);
		return mv;
	}

	public File downloadPdf(String invoiceCode) throws IOException {
		String pdfPath = server.getInvoiceLocation() + "Invoice-" + invoiceCode + ".pdf";
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()) {
			return pdfFile;
		}
		pdfFile.createNewFile();
		pdfUtil.makePdf(company.getAppUrl() + "/invoice/receipt/" + invoiceCode, pdfFile.getAbsolutePath());
		return pdfFile;
	}

}
