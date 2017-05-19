package com.paycr.dashboard.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.PcUser;
import com.paycr.common.validation.RequestValidator;

@Service
public class UserValidator implements RequestValidator<PcUser> {

	@Autowired
	private List<RequestValidator<PcUser>> rules;

	@Override
	public void validate(PcUser user) {
		for (RequestValidator<PcUser> rule : rules) {
			rule.validate(user);
		}
	}

}
