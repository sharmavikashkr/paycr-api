package com.paycr.merchant.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.repository.ConsumerRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidConsumer implements RequestValidator<Consumer> {

	@Autowired
	private ConsumerRepository consRepo;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static final String MOBILE_PATTERN = "^[7-9]{1}[0-9]{9}$";

	private static final String NAME_PATTERN = "[a-zA-Z ]*";

	@Override
	public void validate(Consumer consumer) {
		if (CommonUtil.isNull(consumer) || CommonUtil.isEmpty(consumer.getEmail())
				|| CommonUtil.isEmpty(consumer.getMobile()) || CommonUtil.isEmpty(consumer.getName())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Consumer");
		}
		if (!(match(consumer.getEmail(), EMAIL_PATTERN) && match(consumer.getMobile(), MOBILE_PATTERN)
				&& match(consumer.getName(), NAME_PATTERN))) {
			throw new PaycrException(Constants.FAILURE, "Invalid values of params");
		}
		Consumer extConsumer = consRepo.findConsumerForMerchant(consumer.getMerchant(), consumer.getEmail(),
				consumer.getMobile());
		if (CommonUtil.isNotNull(extConsumer)) {
			throw new PaycrException(Constants.FAILURE, "Consumer already exists");
		}
	}

	private boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

}
