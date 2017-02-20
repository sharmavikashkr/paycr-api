package com.payme.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.payme.common.data.domain.Invoice;
import com.payme.common.data.domain.Merchant;
import com.payme.common.data.domain.PmUser;
import com.payme.common.data.repository.InvoiceRepository;
import com.payme.common.service.SecurityService;

@RestController
public class DashboardController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private InvoiceRepository invRepo;

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("html/index");
	}

	@RequestMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("html/login");
	}

	@Secured({ "ROLE_MERCHANT" })
	@RequestMapping("/dashboard")
	public ModelAndView dashboard() {
		PmUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		List<Invoice> invoices = invRepo.findByMerchant(merchant.getId());
		for (Invoice invoice : invoices) {
			invoice.getItems();
			invoice.getPayment();
			if ("SUCCESS".equals(invoice.getStatus())) {
				invoice.setPaid(true);
			} else {
				invoice.setPaid(false);
			}
		}
		ModelAndView mv = new ModelAndView("html/dashboard");
		mv.addObject("user", user);
		mv.addObject("merchant", merchant);
		mv.addObject("invoices", invoices);
		return mv;
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/admin")
	public ModelAndView admin() {
		PmUser user = secSer.findLoggedInUser();
		ModelAndView mv = new ModelAndView("html/blank");
		mv.addObject("user", user);
		return mv;
	}
}
