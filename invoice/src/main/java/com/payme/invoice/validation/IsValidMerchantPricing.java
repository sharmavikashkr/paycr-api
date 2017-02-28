package com.payme.invoice.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.payme.common.data.domain.Invoice;
import com.payme.common.data.domain.Merchant;
import com.payme.common.data.domain.MerchantPricing;
import com.payme.common.data.domain.Pricing;
import com.payme.common.data.repository.MerchantRepository;
import com.payme.common.exception.PaymeException;
import com.payme.common.type.PricingStatus;
import com.payme.common.util.Constants;
import com.payme.common.validation.RequestValidator;

@Component
@Order(3)
public class IsValidMerchantPricing implements RequestValidator<Invoice> {

	@Autowired
	private MerchantRepository merRepo;

	@Override
	public void validate(Invoice invoice) {
		Merchant merchant = merRepo.findOne(invoice.getMerchant());
		List<MerchantPricing> merPricings = merchant.getPricings();
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
					if (merPricing.getInvoices().size() < pricing.getInvoiceLimit()) {
						amountNotAllowed = false;
						selectedMerPricing = merPricing;
						break;
					}
				}
			}
		}
		if (noActivePlan) {
			throw new PaymeException(Constants.FAILURE, "No active pricing plan found");
		}
		if (amountNotAllowed) {
			throw new PaymeException(Constants.FAILURE, "No active pricing plan for this amount found");
		}
		invoice.setMerchantPricing(selectedMerPricing);
		merRepo.save(merchant);
	}
}
