package com.payme.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.payme.common.data.domain.Invoice;
import com.payme.common.data.repository.InvoiceRepository;
import com.payme.pgclient.client.SSLCheckoutInitiator;

@RestController
public class PaymentController {

	@Autowired
	private SSLCheckoutInitiator checkoutService;

	@Autowired
	private InvoiceRepository invRepo;

	@RequestMapping(value = "{invoiceCode}", method = RequestMethod.GET)
	public ModelAndView payInvoice(@PathVariable String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		return checkoutService.initiate(invoice);
	}
}
