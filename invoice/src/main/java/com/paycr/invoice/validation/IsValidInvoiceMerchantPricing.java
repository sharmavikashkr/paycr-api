package com.paycr.invoice.validation;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.PricingStatus;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(4)
public class IsValidInvoiceMerchantPricing implements RequestValidator<Invoice> {

	@Override
	public void validate(Invoice invoice) {
		List<MerchantPricing> merPricings = invoice.getMerchant().getPricings();
		MerchantPricing selectedMerPricing = null;
		boolean amountNotAllowed = true;
		boolean noActivePlan = true;
		for (MerchantPricing merPricing : merPricings) {
			if (PricingStatus.ACTIVE.equals(merPricing.getStatus())) {
				if (merPricing.getEndDate().compareTo(invoice.getCreated()) < 0) {
					merPricing.setStatus(PricingStatus.INACTIVE);
				}
				noActivePlan = false;
				Pricing pricing = merPricing.getPricing();
				if (invoice.getPayAmount().compareTo(pricing.getStartAmount()) > 0
						&& invoice.getPayAmount().compareTo(pricing.getEndAmount()) <= 0) {
					if (merPricing.getInvCount() < pricing.getInvoiceLimit()) {
						amountNotAllowed = false;
						selectedMerPricing = merPricing;
						break;
					}
				}
			}
		}
		if (noActivePlan) {
			throw new PaycrException(Constants.FAILURE, "No active pricing plan found");
		}
		if (amountNotAllowed) {
			throw new PaycrException(Constants.FAILURE, "No active pricing plan for this amount found");
		}
		invoice.setMerchantPricing(selectedMerPricing);
	}
}
