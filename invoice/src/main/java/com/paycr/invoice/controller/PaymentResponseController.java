package com.paycr.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.type.InvoiceStatus;

@RestController
public class PaymentResponseController {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantRepository merRepo;

	@RequestMapping("/response/{invoiceCode}")
	public ModelAndView successPage(@PathVariable String invoiceCode) {
		try {
			Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
			Merchant merchant = merRepo.findOne(invoice.getMerchant());
			if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
				invoice.setPaid(true);
			}
			ModelAndView mv = new ModelAndView("html/response");
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
