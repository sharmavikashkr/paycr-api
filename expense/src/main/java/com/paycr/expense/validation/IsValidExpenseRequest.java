package com.paycr.expense.validation;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidExpenseRequest implements RequestValidator<Expense> {

	@Autowired
	private ExpenseRepository expRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Override
	public void validate(Expense expense) {
		Date timeNow = new Date();
		String charset = hmacSigner.signWithSecretKey(expense.getMerchant().getSecretKey(),
				String.valueOf(timeNow.getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String expenseCode = expense.getExpenseCode();
		if (expense.isUpdate()) {
			Expense extExpense = expRepo.findByExpenseCode(expenseCode);
			if (StringUtils.isEmpty(expenseCode) || CommonUtil.isNull(extExpense)) {
				throw new PaycrException(Constants.FAILURE, "Expense not found");
			}
			if (ExpenseStatus.PAID.equals(extExpense.getStatus())) {
				throw new PaycrException(Constants.FAILURE, "Expense cannot be modified now");
			}
			expense.setUpdated(timeNow);
		} else {
			do {
				expenseCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
				expense.setExpenseCode(expenseCode);
			} while (CommonUtil.isNotNull(expRepo.findByExpenseCode(expenseCode)));
			expense.setCreated(timeNow);
		}
		if (CommonUtil.isNull(expense.getShipping())) {
			expense.setShipping(new BigDecimal(0));
		}
		if (CommonUtil.isNull(expense.getDiscount())) {
			expense.setDiscount(new BigDecimal(0));
		}
		expense.setStatus(ExpenseStatus.UNPAID);
	}

}
