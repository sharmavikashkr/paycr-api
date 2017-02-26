package com.payme.pgclient.client;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payme.common.data.domain.Invoice;
import com.payme.common.data.domain.Payment;
import com.payme.common.data.repository.InvoiceRepository;

@Service
public class EnquiryInitiator {

	@Autowired
	private InvoiceRepository invRepo;

	public void initiate(Invoice invoice) {
		Payment payment = new Payment();
		payment.setCreated(new Date());
		payment.setInvoice(invoice);
		payment.setPaymentRefNo("abcd");
		payment.setStatus("SUCCESS");
		invoice.setStatus("SUCCESS");
		invRepo.save(invoice);
	}

}
