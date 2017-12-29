package com.paycr.expense.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.util.RoleUtil;
import com.paycr.expense.service.ExpenseService;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

	@Autowired
	private ExpenseService expSer;

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping(value = "/payments/{expenseCode}", method = RequestMethod.GET)
	public List<ExpensePayment> payments(@PathVariable String expenseCode, HttpServletResponse response) {
		try {
			return expSer.payments(expenseCode);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public void refund(@RequestParam(value = "amount", required = true) BigDecimal amount,
			@RequestParam(value = "expenseCode", required = true) String expenseCode, HttpServletResponse response) {
		try {
			expSer.refund(amount, expenseCode);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/markpaid", method = RequestMethod.POST)
	public void markPaid(@RequestBody ExpensePayment payment, HttpServletResponse response) {
		try {
			expSer.markPaid(payment);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping(value = "/{expenseCode}/attachment/new", method = RequestMethod.POST)
	public void addAttachment(@PathVariable String expenseCode, @RequestParam("attach") MultipartFile attach,
			HttpServletResponse response) {
		try {
			expSer.saveAttach(expenseCode, attach);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@RequestMapping(value = "/{expenseCode}/attachment/{attachName:.+}", method = RequestMethod.GET)
	public void getAttachment(@PathVariable String expenseCode, @PathVariable String attachName,
			HttpServletResponse response) {
		try {
			byte[] data = expSer.getAttach(expenseCode, attachName);

			response.setHeader("Content-Disposition", "attachment; filename=\"" + attachName + "\"");
			InputStream is = new ByteArrayInputStream(data);
			IOUtils.copy(is, response.getOutputStream());
			response.setContentLength(data.length);
			response.flushBuffer();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

}
