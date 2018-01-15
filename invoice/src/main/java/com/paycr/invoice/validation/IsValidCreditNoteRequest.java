package com.paycr.invoice.validation;

import java.util.Date;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCreditNote;
import com.paycr.common.data.repository.InvoiceCreditNoteRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidCreditNoteRequest implements RequestValidator<InvoiceCreditNote> {

	@Autowired
	private InvoiceCreditNoteRepository creNoteRepo;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Override
	public void validate(InvoiceCreditNote creditNote) {
		Date timeNow = new Date();
		Invoice invoice = invRepo.findByInvoiceCode(creditNote.getInvoiceCode());
		if (CommonUtil.isNull(invoice)) {
			throw new PaycrException(HttpStatus.BAD_REQUEST_400, "Invalid Invoice");
		}
		if (CommonUtil.isNotNull(invoice.getCreditNote())) {
			throw new PaycrException(HttpStatus.BAD_REQUEST_400, "CreditNote already processed for Invoice");
		}
		String charset = hmacSigner.signWithSecretKey(creditNote.getMerchant().getSecretKey(),
				String.valueOf(timeNow.getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String noteCode = creditNote.getNoteCode();
		do {
			noteCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
			creditNote.setNoteCode(noteCode);
		} while (CommonUtil.isNotNull(creNoteRepo.findByNoteCode(noteCode)));
		creditNote.setCreated(timeNow);
		creditNote.setConsumer(invoice.getConsumer());
	}

}
