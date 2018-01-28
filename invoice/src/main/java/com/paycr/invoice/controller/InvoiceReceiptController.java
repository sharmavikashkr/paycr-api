package com.paycr.invoice.controller;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.invoice.service.InvoiceReceiptService;

@RestController
@RequestMapping("/invoice/receipt")
public class InvoiceReceiptController {

	@Autowired
	private InvoiceReceiptService invRecSer;

	@RequestMapping("/{invoiceCode}")
	public ModelAndView getReceipt(@PathVariable String invoiceCode) throws Exception {
		return invRecSer.getReceiptModelAndView(invoiceCode);
	}

	@RequestMapping("/download/{invoiceCode}")
	public void downloadReceipt(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String invoiceCode) throws Exception {
		File pdfFile = invRecSer.downloadPdf(invoiceCode);

		FileInputStream fis = null;
		byte[] bFile = new byte[(int) pdfFile.length()];
		fis = new FileInputStream(pdfFile);
		fis.read(bFile);
		fis.close();

		response.setHeader("Content-Disposition", "attachment; filename=\"Invoice-" + invoiceCode + ".pdf\"");
		response.setContentType("application/pdf");
		response.getOutputStream().write(bFile);
		response.setContentLength(bFile.length);
		response.flushBuffer();
	}

}
