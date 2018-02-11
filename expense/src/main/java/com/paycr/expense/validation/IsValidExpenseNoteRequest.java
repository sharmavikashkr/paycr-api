package com.paycr.expense.validation;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpenseNote;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidExpenseNoteRequest implements RequestValidator<ExpenseNote> {

	@Autowired
	private ExpenseRepository expRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Override
	public void validate(ExpenseNote note) {
		Date timeNow = new Date();
		Expense expense = expRepo.findByExpenseCode(note.getExpenseCode());
		if (CommonUtil.isNull(expense)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Expense");
		}
		if (CommonUtil.isNotNull(expense.getNote())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Credit/Debit Note already processed for Expense");
		}
		String charset = hmacSigner.signWithSecretKey(note.getMerchant().getSecretKey(),
				String.valueOf(timeNow.getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		if (CommonUtil.isNotNull(expRepo.findByNoteCode(note.getNoteCode()))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Note with this code already exists");
		}
		if (CommonUtil.isNull(note.getAdjustment())) {
			note.setAdjustment(BigDecimal.ZERO);
		}
		note.setCreated(timeNow);
		note.setSupplier(expense.getSupplier());
	}

}
