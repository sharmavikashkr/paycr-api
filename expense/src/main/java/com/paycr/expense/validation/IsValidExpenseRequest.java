package com.paycr.expense.validation;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.util.CommonUtil;
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
		String expenseCode = expense.getExpenseCode();
		if (expense.isUpdate()) {
			Expense extExpense = expRepo.findByExpenseCode(expenseCode);
			if (CommonUtil.isEmpty(expenseCode) || CommonUtil.isNull(extExpense)) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Expense not found");
			}
			if (ExpenseStatus.PAID.equals(extExpense.getStatus())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Expense cannot be modified now");
			}
			expense.setUpdated(timeNow);
		} else {
			if (!CommonUtil.match(expense.getInvoiceCode(), CommonUtil.INVOICE_CODE_PATTERN)) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid invoice code");
			}
			String charset = hmacSigner.signWithSecretKey(expense.getMerchant().getSecretKey(),
					String.valueOf(timeNow.getTime()));
			charset += charset.toLowerCase() + charset.toUpperCase();
			do {
				expenseCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
				expense.setExpenseCode(expenseCode);
			} while (CommonUtil.isNotNull(expRepo.findByExpenseCode(expenseCode)));
			expense.setCreated(timeNow);
		}
		if (CommonUtil.isNull(expense.getShipping())) {
			expense.setShipping(BigDecimal.ZERO);
		}
		if (CommonUtil.isNull(expense.getDiscount())) {
			expense.setDiscount(BigDecimal.ZERO);
		}
		expense.setStatus(ExpenseStatus.UNPAID);
	}

}
