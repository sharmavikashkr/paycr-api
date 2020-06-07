package com.paycr.expense.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.paycr.common.data.domain.ExpenseNote;
import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.util.RoleUtil;
import com.paycr.expense.service.ExpenseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

	@Autowired
	private ExpenseService expSer;

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#expenseCode, 'EXPENSE', 'expenseCode')")
	@GetMapping("/payments/{expenseCode}")
	public List<ExpensePayment> payments(@PathVariable String expenseCode) {
		return expSer.payments(expenseCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#expenseCode, 'EXPENSE', 'expenseCode')")
	@DeleteMapping("/delete/{expenseCode}")
	public void delete(@PathVariable String expenseCode) {
		expSer.delete(expenseCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#expenseCode, 'EXPENSE', 'expenseCode')")
	@PostMapping("/refund")
	public void refund(@RequestParam(value = "amount", required = true) BigDecimal amount,
			@RequestParam(value = "expenseCode", required = true) String expenseCode) {
		expSer.refund(amount, expenseCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PostMapping("/markpaid")
	public void markPaid(@RequestBody ExpensePayment payment) {
		expSer.markPaid(payment);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PostMapping("/note/new")
	public void newNote(@Valid @RequestBody ExpenseNote note) {
		expSer.newNote(note);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH + " && hasPermission(#expenseCode, 'EXPENSE', 'expenseCode')")
	@PostMapping("/{expenseCode}/attachment/new")
	public void addAttachment(@PathVariable String expenseCode, @RequestParam("attach") MultipartFile attach)
			throws IOException {
		expSer.saveAttach(expenseCode, attach);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH + " && hasPermission(#expenseCode, 'EXPENSE', 'expenseCode')")
	@GetMapping("/{accessKey}/{expenseCode}/attachment/{attachName:.+}")
	public void getAttachment(@PathVariable String accessKey, @PathVariable String expenseCode,
			@PathVariable String attachName, HttpServletResponse response) throws IOException {
		byte[] data = expSer.getAttach(accessKey, expenseCode, attachName);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + attachName + "\"");
		response.getOutputStream().write(data);
		response.setContentLength(data.length);
		response.flushBuffer();
	}

}
