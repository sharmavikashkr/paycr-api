package com.paycr.invoice.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.util.PdfUtil;

@Service
public class InvoiceReceiptService {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private Server server;

	@Autowired
	private Company company;

	@Autowired
	private PdfUtil pdfUtil;

	public ModelAndView getReceiptModelAndView(String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		ModelAndView modelAndView = new ModelAndView("html/receipt/invoice_receipt");
		modelAndView.addObject("invoice", invoice);
		modelAndView.addObject("invoice_url", company.getBaseUrl() + "/" + invoiceCode);
		return modelAndView;
	}

	public File downloadPdf(String invoiceCode) throws IOException {
		String pdfPath = server.getInvoiceLocation() + invoiceCode + ".pdf";
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()) {
			return pdfFile;
		}
		pdfFile.createNewFile();
		pdfUtil.makePdf(company.getBaseUrl() + "/invoice/receipt/" + invoiceCode, pdfFile.getAbsolutePath());
		return pdfFile;
	}

}
