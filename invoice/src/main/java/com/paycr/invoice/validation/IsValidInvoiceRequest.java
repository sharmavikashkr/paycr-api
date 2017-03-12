package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidInvoiceRequest implements RequestValidator<Invoice> {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Override
	public void validate(Invoice invoice) {
		Date timeNow = new Date();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		invoice.setMerchant(merchant.getId());
		if (CommonUtil.isNull(invoice.getPayAmount())) {
			throw new PaycrException(Constants.FAILURE, "Amount cannot be null or blank");
		}
		if (invoice.getPayAmount().compareTo(new BigDecimal(0)) <= 0) {
			throw new PaycrException(Constants.FAILURE, "Amount should be greated than 0");
		}
		invoice.setOriginalAmount(invoice.getPayAmount());
		String charset = hmacSigner.signWithSecretKey(merchant.getSecretKey(), String.valueOf(timeNow.getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String invoiceCode = invoice.getInvoiceCode();
		if (StringUtils.isEmpty(invoiceCode) || CommonUtil.isNotNull(invRepo.findByInvoiceCode(invoiceCode))) {
			invoiceCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
			invoice.setInvoiceCode(invoiceCode);
		}
		if (CommonUtil.isNull(invoice.getDiscount())) {
			invoice.setDiscount(new BigDecimal(0));
		}
		if (CommonUtil.isNull(invoice.getShipping())) {
			invoice.setShipping(new BigDecimal(0));
		}
		invoice.setCreated(timeNow);
		invoice.setExpiry(DateUtil.getExpiry(timeNow, invoice.getExpiresIn()));
		invoice.setStatus(InvoiceStatus.UNPAID);
	}

}
