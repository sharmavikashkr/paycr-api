package com.paycr.expense.controller;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.expense.service.ExpenseReceiptService;

@RestController
@RequestMapping("/expense/receipt")
public class ExpenseReceiptController {

	@Autowired
	private ExpenseReceiptService expRecSer;

	@RequestMapping("/{expenseCode}")
	public ModelAndView getReceipt(@PathVariable String expenseCode) throws Exception {
		return expRecSer.getReceiptModelAndView(expenseCode);
	}

	@RequestMapping("/download/{expenseCode}")
	public void downloadReceipt(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String expenseCode) throws Exception {
		File pdfFile = expRecSer.downloadPdf(expenseCode);
		FileInputStream fis = null;
		byte[] bFile = new byte[(int) pdfFile.length()];
		fis = new FileInputStream(pdfFile);
		fis.read(bFile);
		fis.close();

		response.setHeader("Content-Disposition", "attachment; filename=\"Expense-" + expenseCode + ".pdf\"");
		response.setContentType("application/pdf");
		response.getOutputStream().write(bFile);
		response.setContentLength(bFile.length);
		response.flushBuffer();
	}

}
