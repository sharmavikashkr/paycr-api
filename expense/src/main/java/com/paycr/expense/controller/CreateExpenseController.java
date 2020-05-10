package com.paycr.expense.controller;

import javax.validation.Valid;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.util.RoleUtil;
import com.paycr.expense.service.CreateExpenseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expense")
public class CreateExpenseController {

	@Autowired
	private CreateExpenseService crtExpSer;

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PostMapping("/new")
	public Expense single(@Valid @RequestBody Expense expense) {
		return crtExpSer.single(expense);
	}

}
