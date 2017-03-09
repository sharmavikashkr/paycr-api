package com.paycr.invoice.controller;

import java.math.BigDecimal;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.util.CommonUtil;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@RestController
public class PaymentController {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantRepository merRepo;

	@RequestMapping(value = "{invoiceCode}", method = RequestMethod.GET)
	public ModelAndView payInvoice(@PathVariable(value = "invoiceCode") String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (CommonUtil.isNotNull(invoice)) {
			Merchant merchant = merRepo.findOne(invoice.getMerchant());
			ModelAndView mv = new ModelAndView("html/payinvoice");
			mv.addObject("merchantTxnId", "mtx");
			mv.addObject("invoice", invoice);
			mv.addObject("merchant", merchant);
			mv.addObject("rzpKeyId", merchant.getSetting().getRzpKeyId());
			mv.addObject("payAmount", invoice.getPayAmount().multiply(new BigDecimal(100)));
			mv.addObject("consumer", invoice.getConsumer());
			return mv;
		} else {
			return new ModelAndView("html/errorpage");
		}
	}

	@RequestMapping(value = "/return/{invoiceCode}", method = RequestMethod.POST)
	public String purchase(@RequestParam("razorpay_payment_id") String rzpPayId,
			@PathVariable(value = "invoiceCode") String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		if (CommonUtil.isNotNull(invoice)) {
			Merchant merchant = merRepo.findOne(invoice.getMerchant());
			MerchantSetting setting = merchant.getSetting();
			Payment payment = new Payment();
			payment.setCreated(new Date());
			payment.setInvoice(invoice);
			payment.setPaymentRefNo(rzpPayId);

			RazorpayClient razorpay = new RazorpayClient(setting.getRzpKeyId(), setting.getRzpSecretId());
			try {
				com.razorpay.Payment rzpPayment = razorpay.Payments.fetch(rzpPayId);
				JSONObject request = new JSONObject();
				request.put("amount", rzpPayment.get("amount").toString());
				try {
					rzpPayment = razorpay.Payments.capture(rzpPayId, request);
				} catch (Exception ex) {
					System.out.println("This payment has already been captured");
				}
				payment.setStatus(rzpPayment.get("status"));
				invoice.setStatus(getStatus(rzpPayment.get("status")));
				payment.setMethod(rzpPayment.get("method"));
				payment.setBank(JSONObject.NULL.equals(rzpPayment.get("bank")) ? null : rzpPayment.get("bank"));
				payment.setWallet(JSONObject.NULL.equals(rzpPayment.get("wallet")) ? null : rzpPayment.get("wallet"));
				invoice.setPayment(payment);
				invRepo.save(invoice);
				return "SUCCESS";
			} catch (RazorpayException e) {
				System.out.println(e.getMessage());
				return "FAILURE";
			}
		} else {
			return "FAILURE";
		}
	}

	private String getStatus(String rzpStatus) {
		if ("captured".equals(rzpStatus)) {
			return "Paid";
		} else {
			return "Unpaid";
		}
	}
}
