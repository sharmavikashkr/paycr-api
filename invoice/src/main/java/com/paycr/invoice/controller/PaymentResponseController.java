package com.paycr.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.InvoiceRepository;

@RestController
public class PaymentResponseController {

	@Autowired
	private InvoiceRepository invRepo;

	@RequestMapping("/response/{invoiceCode}")
	public ModelAndView successPage(@PathVariable String invoiceCode) {
		try {
			Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
			Merchant merchant = invoice.getMerchant();
			ModelAndView mv = new ModelAndView("html/inv-response");
			mv.addObject("invoice", invoice);
			mv.addObject("merchant", merchant);
			return mv;
		} catch (Exception ex) {
			ModelAndView mv = new ModelAndView("html/errorpage");
			mv.addObject("message", "Requested Resource is not found");
			return mv;
		}
	}
}
