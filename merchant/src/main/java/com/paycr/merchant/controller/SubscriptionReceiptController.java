package com.paycr.merchant.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.merchant.service.SubscriptionReceiptService;

@RestController
@RequestMapping("/subscription/receipt")
public class SubscriptionReceiptController {

	@Autowired
	private SubscriptionReceiptService subsRecSer;

	@GetMapping("/{subscriptionCode}")
	public ModelAndView receipt(@PathVariable String subscriptionCode) {
		return subsRecSer.getSubscriptionReceipt(subscriptionCode);
	}

	@GetMapping("/pricing/{pricingId}")
	public void getSubscription(@PathVariable Integer pricingId, HttpServletResponse response) throws IOException {
		String subsCode = subsRecSer.getPricingReceipt(pricingId);
		response.sendRedirect("/subscription/receipt/" + subsCode);
	}

	@GetMapping("/download/{subscriptionCode}")
	public void download(@PathVariable String subscriptionCode, HttpServletResponse response) throws IOException {
		File pdfFile = subsRecSer.downloadPdf(subscriptionCode);
		response.setContentType("application/pdf");

		FileInputStream fis = null;
		byte[] bFile = new byte[(int) pdfFile.length()];
		fis = new FileInputStream(pdfFile);
		fis.read(bFile);
		fis.close();

		response.setHeader("Content-Disposition",
				"attachment; filename=\"SubscriptionReceipt-" + subscriptionCode + ".pdf\"");
		response.setContentType("application/pdf");
		response.getOutputStream().write(bFile);
		response.setContentLength(bFile.length);
		response.flushBuffer();
	}

}
