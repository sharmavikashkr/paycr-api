package com.paycr.dashboard.validation;

import org.springframework.stereotype.Component;

import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
public class IsValidGstinRequest implements RequestValidator<String> {

	private static final String GSTIN_PATTERN = "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}";

	@Override
	public void validate(String gstin) {
		if (!CommonUtil.isEmpty(gstin) && !match(gstin, GSTIN_PATTERN)) {
			throw new PaycrException(Constants.FAILURE, "Invalid GSTIN");
		}
	}

	private boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

}
