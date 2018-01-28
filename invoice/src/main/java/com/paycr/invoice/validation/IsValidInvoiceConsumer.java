package com.paycr.invoice.validation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.repository.ConsumerRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ConsumerType;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(1)
public class IsValidInvoiceConsumer implements RequestValidator<Invoice> {

	@Autowired
	private ConsumerRepository consRepo;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static final String MOBILE_PATTERN = "^[7-9]{1}[0-9]{9}$";

	private static final String NAME_PATTERN = "[a-zA-Z ]*";

	@Override
	public void validate(Invoice invoice) {
		if (InvoiceType.BULK.equals(invoice.getInvoiceType())) {
			invoice.setConsumer(null);
			return;
		}
		Consumer consumer = invoice.getConsumer();
		if (CommonUtil.isNull(consumer)) {
			throw new PaycrException(Constants.FAILURE, "Invalid Consumer");
		}
		if (!(match(consumer.getEmail(), EMAIL_PATTERN) || match(consumer.getMobile(), MOBILE_PATTERN)
				|| match(consumer.getName(), NAME_PATTERN))) {
			throw new PaycrException(Constants.FAILURE, "Invalid values of params");
		}
		Consumer exstConsumer = consRepo.findConsumerForMerchant(invoice.getMerchant(), consumer.getEmail(),
				consumer.getMobile());
		if (CommonUtil.isNotNull(exstConsumer) && !exstConsumer.isActive()) {
			throw new PaycrException(Constants.FAILURE, "Consumer not active");
		}
		if (CommonUtil.isNull(exstConsumer)) {
			consumer.setMerchant(invoice.getMerchant());
			consumer.setActive(true);
			consumer.setCreated(new Date());
			consumer.setEmailOnPay(true);
			consumer.setEmailOnRefund(true);
			if (consumer.getCreatedBy() == null) {
				consumer.setCreatedBy(invoice.getCreatedBy());
			}
			consumer.setType(ConsumerType.CUSTOMER);
			exstConsumer = consRepo.save(consumer);
		}
		invoice.setConsumer(exstConsumer);
	}

	private boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

}
