package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
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
	private InvoiceRepository invRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Override
	public void validate(Invoice invoice) {
		Date timeNow = new Date();
		String charset = hmacSigner.signWithSecretKey(invoice.getMerchant().getSecretKey(),
				String.valueOf(timeNow.getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String invoiceCode = invoice.getInvoiceCode();
		if (invoice.isUpdate()) {
			Invoice extInvoice = invRepo.findByInvoiceCode(invoiceCode);
			if (StringUtils.isEmpty(invoiceCode) || CommonUtil.isNull(extInvoice)) {
				throw new PaycrException(Constants.FAILURE, "Invoice not found");
			}
			if (!InvoiceType.SINGLE.equals(extInvoice.getInvoiceType())) {
				throw new PaycrException(Constants.FAILURE, "Only Single invoices can be modified");
			}
			if (InvoiceStatus.EXPIRED.equals(extInvoice.getStatus())
					|| InvoiceStatus.PAID.equals(extInvoice.getStatus())) {
				throw new PaycrException(Constants.FAILURE, "Invoice cannot be modified now");
			}
			if (!invoice.getInvoiceType().equals(extInvoice.getInvoiceType())) {
				throw new PaycrException(Constants.FAILURE, "Invoice type cannot be modified");
			}
			invoice.setParent(extInvoice.getParent());
			invoice.setUpdated(timeNow);
		} else {
			do {
				invoiceCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
				invoice.setInvoiceCode(invoiceCode);
			} while (CommonUtil.isNotNull(invRepo.findByInvoiceCode(invoiceCode)));
			invoice.setCreated(timeNow);
			if (!invoice.isNeverExpire()) {
				invoice.setExpiry(DateUtil.getExpiry(timeNow, invoice.getExpiresIn()));
			} else {
				invoice.setExpiry(null);
				invoice.setExpiresIn(-1);
			}
			invoice.setNotices(null);
			invoice.setNote(null);
			invoice.setPayment(null);
		}
		if (CommonUtil.isNull(invoice.getShipping())) {
			invoice.setShipping(new BigDecimal(0));
		}
		if (CommonUtil.isNull(invoice.getDiscount())) {
			invoice.setDiscount(new BigDecimal(0));
		}
		if (CommonUtil.isNull(invoice.getInvoiceType())) {
			invoice.setInvoiceType(InvoiceType.SINGLE);
		}
		invoice.setStatus(InvoiceStatus.CREATED);
		if (CommonUtil.isNotNull(invoice.getNotices())) {
			for (InvoiceNotify invNot : invoice.getNotices()) {
				invNot.setInvoice(invoice);
			}
		}
	}

}
