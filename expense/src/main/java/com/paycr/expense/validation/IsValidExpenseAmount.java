package com.paycr.expense.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.http.HttpStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpenseItem;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(3)
public class IsValidExpenseAmount implements RequestValidator<Expense> {

	@Override
	public void validate(Expense expense) {
		if (CommonUtil.isNull(expense.getPayAmount())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Amount cannot be null or blank");
		}
		if (expense.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Amount should be greated than 0");
		}
		if (expense.isAddItems()) {
			BigDecimal totalRate = BigDecimal.ZERO;
			BigDecimal totalPrice = BigDecimal.ZERO;
			for (ExpenseItem item : expense.getItems()) {
				totalPrice = totalPrice.add(item.getPrice()).setScale(2, RoundingMode.HALF_UP);
				totalRate = totalRate.add(item.getAsset().getRate().multiply(BigDecimal.valueOf(item.getQuantity())))
						.setScale(2, RoundingMode.HALF_UP);
			}
			if (totalRate.compareTo(expense.getTotal().setScale(2, RoundingMode.HALF_UP)) != 0
					|| totalPrice.compareTo(expense.getTotalPrice().setScale(2, RoundingMode.HALF_UP)) != 0) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Items do not amount to total");
			}
		}
		BigDecimal finalAmount = expense.getTotalPrice().add(expense.getShipping()).subtract(expense.getDiscount());
		if (finalAmount.setScale(2, RoundingMode.HALF_UP)
				.compareTo(expense.getPayAmount().setScale(2, RoundingMode.HALF_UP)) != 0) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Amount calculation mismatch");
		}
	}

}
