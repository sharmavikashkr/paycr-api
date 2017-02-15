package com.payme.invoice.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.payme.common.data.domain.Invoice;
import com.payme.common.data.domain.Merchant;
import com.payme.common.data.repository.MerchantRepository;
import com.payme.common.exception.PaymeException;
import com.payme.common.util.CommonUtil;
import com.payme.common.util.Constants;
import com.payme.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidMerchant implements RequestValidator<Invoice> {

	@Autowired
	private MerchantRepository merRepo;

	@Override
	public void validate(Invoice invoice) {
		Merchant merchant = merRepo.findByAccessKey(invoice.getMerchant());
		if (CommonUtil.isNull(merchant)) {
			throw new PaymeException(Constants.FAILURE, "Invalid Merchant");
		}
	}

}
