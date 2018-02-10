package com.paycr.expense.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.data.domain.ExpenseNote;
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
	public List<ExpensePayment> payments(@PathVariable String expenseCode) {
		return expSer.payments(expenseCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public void refund(@RequestParam(value = "amount", required = true) BigDecimal amount,
			@RequestParam(value = "expenseCode", required = true) String expenseCode) {
		expSer.refund(amount, expenseCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/markpaid", method = RequestMethod.POST)
	public void markPaid(@RequestBody ExpensePayment payment) {
		expSer.markPaid(payment);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/note/new", method = RequestMethod.POST)
	public void newNote(@Valid @RequestBody ExpenseNote note) {
		expSer.newNote(note);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping(value = "/{expenseCode}/attachment/new", method = RequestMethod.POST)
	public void addAttachment(@PathVariable String expenseCode, @RequestParam("attach") MultipartFile attach)
			throws IOException {
		expSer.saveAttach(expenseCode, attach);
	}

	@RequestMapping(value = "/{expenseCode}/attachment/{attachName:.+}", method = RequestMethod.GET)
	public void getAttachment(@PathVariable String expenseCode, @PathVariable String attachName,
			HttpServletResponse response) throws IOException {
		byte[] data = expSer.getAttach(expenseCode, attachName);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + attachName + "\"");
		response.getOutputStream().write(data);
		response.setContentLength(data.length);
		response.flushBuffer();
	}

}
