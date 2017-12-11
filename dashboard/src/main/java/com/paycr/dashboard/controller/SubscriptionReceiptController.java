package com.paycr.dashboard.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.dashboard.service.SubscriptionReceiptService;

@RestController
@RequestMapping("/subscription/receipt")
public class SubscriptionReceiptController {

	@Autowired
	private SubscriptionReceiptService subsRecSer;

	@RequestMapping(value = "/{subscriptionCode}", method = RequestMethod.GET)
	public ModelAndView receipt(@PathVariable String subscriptionCode, HttpServletResponse response) {
		try {
			return subsRecSer.getSubscriptionReceipt(subscriptionCode);
		} catch (Exception ex) {
			throw new PaycrException(Constants.FAILURE, "Subscription not found");
		}
	}

	@RequestMapping(value = "/download/{subscriptionCode}", method = RequestMethod.GET)
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
		InputStream is = new ByteArrayInputStream(bFile);
		IOUtils.copy(is, response.getOutputStream());
		response.setContentLength(bFile.length);
		response.flushBuffer();
	}

}
