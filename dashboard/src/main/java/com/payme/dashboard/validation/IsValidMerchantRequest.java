package com.payme.dashboard.validation;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.payme.common.data.domain.Merchant;
import com.payme.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidMerchantRequest implements RequestValidator<Merchant> {

	@Override
	public void validate(Merchant merchant) {
	}

}
