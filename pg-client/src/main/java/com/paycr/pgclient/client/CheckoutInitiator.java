package com.paycr.pgclient.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.MerchantRepository;

@Service
public class CheckoutInitiator {

	@Autowired
	private MerchantRepository merRepo;

	public ModelAndView initiate(Invoice invoice) {
		Merchant merchant = merRepo.findOne(invoice.getMerchant());
		ModelAndView mv = new ModelAndView("html/payinvoice");
		mv.addObject("merchantTxnId", "mtx");
		mv.addObject("invoice", invoice);
		mv.addObject("merchant", merchant);
		return mv;
	}

}
