package com.paycr.merchant.validation;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
public class IsValidGstinRequest implements RequestValidator<String> {

	@Override
	public void validate(String gstin) {
		if (!CommonUtil.isEmpty(gstin) && !CommonUtil.match(gstin, CommonUtil.GSTIN_PATTERN)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid GSTIN");
		}
	}

}
