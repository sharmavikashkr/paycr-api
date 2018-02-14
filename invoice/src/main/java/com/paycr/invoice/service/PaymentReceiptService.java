package com.paycr.invoice.service;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.util.PdfUtil;

@Service
public class PaymentReceiptService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentReceiptService.class);

	@Autowired
	private Server server;

	@Autowired
	private Company company;

	@Autowired
	private PdfUtil pdfUtil;

	public File downloadPdf(String invoiceCode) throws IOException {
		logger.info("Invoice payment receipt download : {}", invoiceCode);
		String pdfPath = server.getPaymentLocation() + invoiceCode + ".pdf";
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()) {
			return pdfFile;
		}
		pdfFile.createNewFile();
		pdfUtil.makePdf(company.getAppUrl() + "/payment/response/" + invoiceCode + "?show=false",
				pdfFile.getAbsolutePath());
		return pdfFile;
	}

}
