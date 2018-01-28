package com.paycr.invoice.controller;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.invoice.service.InvoiceService;
import com.paycr.invoice.service.PaymentReceiptService;

@RestController
@RequestMapping("/payment")
public class PaymentResponseController {

	@Autowired
	private InvoiceService invSer;

	@Autowired
	private PaymentReceiptService payRecSer;

	@Autowired
	private Company company;

	@RequestMapping("/response/{invoiceCode}")
	public ModelAndView successPage(@PathVariable String invoiceCode,
			@RequestParam(value = "show", required = false) Boolean show) {
		Invoice invoice = invSer.getInvoice(invoiceCode);
		Merchant merchant = invoice.getMerchant();
		ModelAndView mv = new ModelAndView("html/inv-response");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("invoice", invoice);
		mv.addObject("merchant", merchant);
		show = (show != null) ? show : true;
		mv.addObject("show", show);
		return mv;
	}

	@RequestMapping("/receipt/download/{invoiceCode}")
	public void downloadReceipt(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String invoiceCode) throws Exception {
		File pdfFile = payRecSer.downloadPdf(invoiceCode);
		response.setContentType("application/pdf");

		FileInputStream fis = null;
		byte[] bFile = new byte[(int) pdfFile.length()];
		fis = new FileInputStream(pdfFile);
		fis.read(bFile);
		fis.close();

		response.setHeader("Content-Disposition", "attachment; filename=\"PaymentReceipt-" + invoiceCode + ".pdf\"");
		response.setContentType("application/pdf");
		response.getOutputStream().write(bFile);
		response.setContentLength(bFile.length);
		response.flushBuffer();
	}
}
