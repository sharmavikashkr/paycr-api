package com.paycr.invoice.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.exception.PaycrException;
import com.paycr.invoice.service.PaymentService;

@RestController
public class PaymentController {

	@Autowired
	private PaymentService paySer;

	@Autowired
	private Company company;

	@RequestMapping(value = "{invoiceCode}", method = RequestMethod.GET)
	public ModelAndView payInvoice(@PathVariable(value = "invoiceCode") String invoiceCode) {
		try {
			return paySer.payInvoice(invoiceCode);
		} catch (PaycrException pex) {
			ModelAndView mv = new ModelAndView("html/errorpage");
			mv.addObject("staticUrl", company.getStaticUrl());
			mv.addObject("message", pex.getMessage());
			return mv;
		}
	}

	@RequestMapping(value = "/updateConsumerAndPay/{invoiceCode}", method = RequestMethod.POST)
	public void updateConsumerAndPay(@PathVariable(value = "invoiceCode") String invoiceCode,
			@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("mobile") String mobile, @RequestParam("signature") String signature,
			HttpServletResponse response) throws IOException {
		try {
			invoiceCode = paySer.updateConsumerAndPay(invoiceCode, name, email, mobile, signature);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		response.sendRedirect("/" + invoiceCode);
	}

	@RequestMapping("/payment/decline/{invoiceCode}")
	public void decline(@PathVariable String invoiceCode, HttpServletResponse response) throws IOException {
		try {
			paySer.decline(invoiceCode);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		response.sendRedirect("/payment/response/" + invoiceCode);
	}

	@RequestMapping(value = "/payment/return/{invoiceCode}", method = RequestMethod.POST)
	public void purchase(@RequestParam Map<String, String> formData, HttpServletResponse response) throws IOException {
		String invoiceCode = null;
		try {
			invoiceCode = paySer.purchase(formData);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		response.sendRedirect("/payment/response/" + invoiceCode);
	}
}
