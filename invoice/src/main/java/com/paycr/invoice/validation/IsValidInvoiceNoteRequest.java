package com.paycr.invoice.validation;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidInvoiceNoteRequest implements RequestValidator<InvoiceNote> {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Override
	public void validate(InvoiceNote note) {
		Date timeNow = new Date();
		Invoice invoice = invRepo.findByInvoiceCode(note.getInvoiceCode());
		if (CommonUtil.isNull(invoice)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Invoice");
		}
		if (CommonUtil.isNotNull(invoice.getNote())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Credit/Debit Note already processed for Invoice");
		}
		String charset = hmacSigner.signWithSecretKey(note.getMerchant().getSecretKey(),
				String.valueOf(timeNow.getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String noteCode = note.getNoteCode();
		do {
			noteCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
			note.setNoteCode(noteCode);
		} while (CommonUtil.isNotNull(invRepo.findByNoteCode(noteCode)));
		if (CommonUtil.isNull(note.getAdjustment())) {
			note.setAdjustment(BigDecimal.ZERO);
		}
		note.setCreated(timeNow);
		note.setConsumer(invoice.getConsumer());
	}

}
