package com.paycr.invoice.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.paycr.common.bean.Company;
import com.paycr.common.exception.PaycrException;
import com.paycr.invoice.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private PaymentService paySer;

	@Autowired
	private Company company;

	@GetMapping("{invoiceCode}")
	public ModelAndView payInvoice(@PathVariable(value = "invoiceCode") String invoiceCode) {
		try {
			return paySer.payInvoice(invoiceCode);
		} catch (Exception ex) {
			String message = (ex instanceof PaycrException) ? ex.getMessage() : "Resource not found";
			ModelAndView mv = new ModelAndView("html/errorpage");
			mv.addObject("staticUrl", company.getStaticUrl());
			mv.addObject("webUrl", company.getWebUrl());
			mv.addObject("message", message);
			return mv;
		}
	}

	@PostMapping("/updateConsumerAndPay/{invoiceCode}")
	public void updateConsumerAndPay(@PathVariable(value = "invoiceCode") String invoiceCode,
			@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("mobile") String mobile, @RequestParam("signature") String signature,
			HttpServletResponse response) throws IOException {
		invoiceCode = paySer.updateConsumerAndPay(invoiceCode, name, email, mobile, signature);
		response.sendRedirect("/payment/" + invoiceCode);
	}

	@GetMapping("/decline/{invoiceCode}")
	public void decline(@PathVariable String invoiceCode, HttpServletResponse response) throws IOException {
		paySer.decline(invoiceCode);
		response.sendRedirect("/payment/response/" + invoiceCode);
	}

	@PostMapping("/return/{invoiceCode}")
	public void purchase(@RequestParam Map<String, String> formData, HttpServletResponse response)
			throws IOException, Exception {
		String invoiceCode = null;
		invoiceCode = paySer.purchase(formData);
		response.sendRedirect("/payment/response/" + invoiceCode);
	}
}
