package com.payme.pgclient.client;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.payme.common.data.domain.Invoice;

@Service
public class SSLCheckoutInitiator {

	public ModelAndView initiate(Invoice invoice) {
		ModelAndView mv = new ModelAndView("html/payinvoice");
		mv.addObject("merchantTxnId", "mtx");
		mv.addObject("amount", invoice.getAmount());
		mv.addObject("currency", invoice.getCurrency());
		mv.addObject("checkout", invoice.getCurrency());
		return mv;
	}

}
