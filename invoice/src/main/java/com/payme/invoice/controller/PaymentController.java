package com.payme.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.payme.common.data.domain.Invoice;
import com.payme.common.data.repository.InvoiceRepository;
import com.payme.common.util.CommonUtil;
import com.payme.pgclient.client.CheckoutInitiator;

@RestController
public class PaymentController {

	@Autowired
	private CheckoutInitiator checkoutService;

	@Autowired
	private InvoiceRepository invRepo;

	@RequestMapping(value = "{invoiceCode}", method = RequestMethod.GET)
	public ModelAndView payInvoice(@PathVariable(value = "invoiceCode") String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (CommonUtil.isNotNull(invoice)) {
			return checkoutService.initiate(invoice);
		} else {
			return new ModelAndView("html/errorpage");
		}
	}
}
