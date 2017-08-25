package com.paycr.dashboard.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.validation.RequestValidator;

@Service
public class ConsumerValidator implements RequestValidator<Consumer> {

	@Autowired
	private List<RequestValidator<Consumer>> rules;

	@Override
	public void validate(Consumer consumer) {
		for (RequestValidator<Consumer> rule : rules) {
			rule.validate(consumer);
		}
	}

}
