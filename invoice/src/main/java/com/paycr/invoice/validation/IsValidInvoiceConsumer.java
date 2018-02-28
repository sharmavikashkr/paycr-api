package com.paycr.invoice.validation;

import java.util.Date;

import org.apache.http.HttpStatus;
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
import com.paycr.common.validation.RequestValidator;

@Component
@Order(1)
public class IsValidInvoiceConsumer implements RequestValidator<Invoice> {

	@Autowired
	private ConsumerRepository consRepo;

	@Override
	public void validate(Invoice invoice) {
		if (InvoiceType.BULK.equals(invoice.getInvoiceType())) {
			invoice.setConsumer(null);
			return;
		}
		Consumer consumer = invoice.getConsumer();
		if (CommonUtil.isNull(consumer)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Consumer");
		}
		if (!(CommonUtil.match(consumer.getEmail(), CommonUtil.EMAIL_PATTERN)
				|| CommonUtil.match(consumer.getMobile(), CommonUtil.MOBILE_PATTERN)
				|| CommonUtil.match(consumer.getName(), CommonUtil.NAME_PATTERN))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid values of params");
		}
		Consumer exstConsumer = consRepo.findConsumerForMerchant(invoice.getMerchant(), consumer.getEmail(),
				consumer.getMobile());
		if (CommonUtil.isNotNull(exstConsumer) && !exstConsumer.isActive()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Consumer not active");
		}
		if (CommonUtil.isNull(exstConsumer)) {
			consumer.setMerchant(invoice.getMerchant());
			consumer.setActive(true);
			consumer.setCreated(new Date());
			consumer.setEmailOnPay(true);
			consumer.setEmailOnRefund(true);
			consumer.setEmailNote(true);
			if (CommonUtil.isNull(consumer.getCreatedBy())) {
				consumer.setCreatedBy(invoice.getCreatedBy());
			}
			consumer.setType(ConsumerType.CUSTOMER);
			exstConsumer = consRepo.save(consumer);
		}
		invoice.setConsumer(exstConsumer);
	}

}
