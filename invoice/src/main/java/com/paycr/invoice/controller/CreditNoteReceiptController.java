package com.paycr.invoice.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.invoice.service.CreditNoteReceiptService;

@RestController
@RequestMapping("/creditNote/receipt")
public class CreditNoteReceiptController {

	@Autowired
	private CreditNoteReceiptService creditNoteRecSer;

	@RequestMapping("/{noteCode}")
	public ModelAndView getReceipt(@PathVariable String noteCode) throws Exception {
		return creditNoteRecSer.getReceiptModelAndView(noteCode);
	}

	@RequestMapping("/download/{noteCode}")
	public void downloadReceipt(HttpServletRequest request, HttpServletResponse response, @PathVariable String NoteCode)
			throws Exception {
		File pdfFile = creditNoteRecSer.downloadPdf(NoteCode);

		FileInputStream fis = null;
		byte[] bFile = new byte[(int) pdfFile.length()];
		fis = new FileInputStream(pdfFile);
		fis.read(bFile);
		fis.close();

		response.setHeader("Content-Disposition", "attachment; filename=\"CreditNote-" + NoteCode + ".pdf\"");
		response.setContentType("application/pdf");
		InputStream is = new ByteArrayInputStream(bFile);
		IOUtils.copy(is, response.getOutputStream());
		response.setContentLength(bFile.length);
		response.flushBuffer();
	}

}
