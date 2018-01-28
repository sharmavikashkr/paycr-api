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

import com.paycr.invoice.service.NoteReceiptService;

@RestController
@RequestMapping("/note/receipt")
public class NoteReceiptController {

	@Autowired
	private NoteReceiptService noteRecSer;

	@RequestMapping("/{noteCode}")
	public ModelAndView getReceipt(@PathVariable String noteCode) throws Exception {
		return noteRecSer.getReceiptModelAndView(noteCode);
	}

	@RequestMapping("/download/{noteCode}")
	public void downloadReceipt(HttpServletRequest request, @PathVariable String NoteCode, HttpServletResponse response)
			throws Exception {
		File pdfFile = noteRecSer.downloadPdf(NoteCode);

		FileInputStream fis = null;
		byte[] bFile = new byte[(int) pdfFile.length()];
		fis = new FileInputStream(pdfFile);
		fis.read(bFile);
		fis.close();

		response.setHeader("Content-Disposition", "attachment; filename=\"Note-" + NoteCode + ".pdf\"");
		response.setContentType("application/pdf");
		response.getOutputStream().write(bFile);
		response.setContentLength(bFile.length);
		response.flushBuffer();
	}

}
