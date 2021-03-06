package com.paycr.dashboard.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.validation.RequestValidator;

@Service
public class MerchantValidator implements RequestValidator<Merchant> {

	@Autowired
	private List<RequestValidator<Merchant>> rules;

	@Override
	public void validate(Merchant merchant) {
		for (RequestValidator<Merchant> rule : rules) {
			rule.validate(merchant);
		}
	}

}
