package com.paycr.merchant.validation;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.repository.ConsumerRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidConsumer implements RequestValidator<Consumer> {

	@Autowired
	private ConsumerRepository consRepo;

	@Override
	public void validate(Consumer consumer) {
		if (CommonUtil.isNull(consumer) || CommonUtil.isEmpty(consumer.getEmail())
				|| CommonUtil.isEmpty(consumer.getMobile()) || CommonUtil.isEmpty(consumer.getName())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Consumer");
		}
		if (!(CommonUtil.match(consumer.getEmail(), CommonUtil.EMAIL_PATTERN)
				&& CommonUtil.match(consumer.getMobile(), CommonUtil.MOBILE_PATTERN)
				&& CommonUtil.match(consumer.getName(), CommonUtil.NAME_PATTERN))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid values of params");
		}
		Consumer extConsumer = consRepo.findConsumerForMerchant(consumer.getMerchant(), consumer.getEmail(),
				consumer.getMobile());
		if (CommonUtil.isNotNull(extConsumer)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Consumer already exists");
		}
	}

}
