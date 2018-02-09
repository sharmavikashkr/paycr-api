package com.paycr.expense.validation;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.PricingStatus;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(4)
public class IsValidExpenseMerchantPricing implements RequestValidator<Expense> {

	@Autowired
	private MerchantRepository merRepo;

	@Override
	public void validate(Expense expense) {
		if (expense.isUpdate()) {
			return;
		}
		List<MerchantPricing> merPricings = expense.getMerchant().getPricings();
		MerchantPricing selectedMerPricing = null;
		boolean noActivePlan = true;
		boolean hasPlanChanged = false;
		for (MerchantPricing merPricing : merPricings) {
			if (PricingStatus.ACTIVE.equals(merPricing.getStatus())) {
				if (merPricing.getEndDate().compareTo(expense.getCreated()) < 0
						|| merPricing.getUseCount() >= merPricing.getPricing().getLimit() * merPricing.getQuantity()) {
					merPricing.setStatus(PricingStatus.INACTIVE);
					hasPlanChanged = true;
					continue;
				} else {
					selectedMerPricing = merPricing;
					noActivePlan = false;
				}
			}
		}
		if (hasPlanChanged) {
			merRepo.save(expense.getMerchant());
		}
		if (noActivePlan) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "No active pricing plan found");
		}
		expense.setMerchantPricing(selectedMerPricing);
	}
}
