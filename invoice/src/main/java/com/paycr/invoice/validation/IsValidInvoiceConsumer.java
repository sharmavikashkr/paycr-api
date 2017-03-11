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

	@Override
	public void validate(Invoice invoice) {
		if (CommonUtil.isNull(invoice.getConsumer())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Consumer");
		}
		if (CommonUtil.isEmpty(invoice.getConsumer().getEmail())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Consumer Email");
		}
		if (CommonUtil.isEmpty(invoice.getConsumer().getName())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Consumer Name");
		}
		if (CommonUtil.isEmpty(invoice.getConsumer().getMobile())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Consumer Mobile");
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

}
