package com.paycr.invoice.validation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.repository.ConsumerRepository;
import com.paycr.common.exception.PaycrException;
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
		if (CommonUtil.isNull(invoice.getConsumer())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Consumer");
		}
		if (!(match(invoice.getConsumer().getEmail(), EMAIL_PATTERN)
				|| match(invoice.getConsumer().getMobile(), MOBILE_PATTERN)
				|| match(invoice.getConsumer().getName(), NAME_PATTERN))) {
			throw new PaycrException(Constants.FAILURE, "Invalid values of params");
		}
		Consumer consumer = consRepo.findByEmailAndMobile(invoice.getConsumer().getEmail(),
				invoice.getConsumer().getMobile());
		if (CommonUtil.isNull(consumer)) {
			invoice.getConsumer().setActive(true);
			invoice.getConsumer().setCreated(new Date());
			consumer = consRepo.save(invoice.getConsumer());
		}
		invoice.setConsumer(consumer);
	}

	private boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

}
