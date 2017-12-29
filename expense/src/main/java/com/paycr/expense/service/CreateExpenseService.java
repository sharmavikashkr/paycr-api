package com.paycr.expense.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.service.SecurityService;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.ObjectType;
import com.paycr.expense.validation.ExpenseValidator;

@Service
public class CreateExpenseService {
	@Autowired
	private SecurityService secSer;

	@Autowired
	private ExpenseRepository expRepo;

	@Autowired
	private ExpenseValidator expValidator;

	@Autowired
	private TimelineService tlService;

	public Expense single(Expense expense) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		expense.setMerchant(merchant);
		expense.setCreatedBy(user.getEmail());
		if (expense.isUpdate()) {
			expense.setUpdatedBy(user.getEmail());
		}
		expValidator.validate(expense);
		expRepo.save(expense);
		if (expense.isUpdate()) {
			tlService.saveToTimeline(expense.getId(), ObjectType.EXPENSE, "Expense updated", true, user.getEmail());
		} else {
			tlService.saveToTimeline(expense.getId(), ObjectType.EXPENSE, "Expense created", true, user.getEmail());
		}
		return expense;
	}

}
