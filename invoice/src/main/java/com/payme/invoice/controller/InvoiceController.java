package com.payme.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.payme.common.bean.Payme;
import com.payme.common.data.domain.Invoice;
import com.payme.common.data.repository.InvoiceRepository;
import com.payme.invoice.validation.InvoiceValidator;

@RestController
@RequestMapping("invoice")
public class InvoiceController {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private Payme payme;

	@Autowired
	private InvoiceValidator invValidator;

	@Secured({ "ROLE_MERCHANT" })
	@RequestMapping(value = "new", method = RequestMethod.POST)
	public String single(@RequestBody Invoice invoice) {
		try {
			invValidator.validate(invoice);
		} catch (Exception ex) {
			return ex.getMessage();
		}
		invRepo.save(invoice);
		return "Invoice Generated : " + payme.getBaseUrl() + "/" + invoice.getInvoiceCode();
	}

}
