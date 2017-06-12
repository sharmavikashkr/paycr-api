package com.paycr.invoice.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCustomParam;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@RestController
public class PaymentController {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private NotificationRepository notiRepo;

	@RequestMapping(value = "{invoiceCode}", method = RequestMethod.GET)
	public ModelAndView payInvoice(@PathVariable(value = "invoiceCode") String invoiceCode) {
		try {
			Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
			validate(invoice);
			Merchant merchant = merRepo.findOne(invoice.getMerchant());
			ModelAndView mv = new ModelAndView("html/payinvoice");
			mv.addObject("invoice", invoice);
			mv.addObject("merchant", merchant);
			mv.addObject("rzpKeyId", merchant.getSetting().getRzpKeyId());
			mv.addObject("payAmount", String.valueOf(invoice.getPayAmount().multiply(new BigDecimal(100))));
			mv.addObject("consumer", invoice.getConsumer());
			return mv;
		} catch (PaycrException pex) {
			ModelAndView mv = new ModelAndView("html/errorpage");
			mv.addObject("message", pex.getMessage());
			return mv;
		}
	}

	private void validate(Invoice invoice) {
		if (CommonUtil.isNull(invoice)) {
			throw new PaycrException(Constants.FAILURE, "Requested Resource is not found");
		}
		if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "This invoice is already paid");
		}
		if (InvoiceStatus.EXPIRED.equals(invoice.getStatus()) && !InvoiceStatus.PAID.equals(invoice.getStatus())) {
			throw new PaycrException(Constants.FAILURE, "This invoice has expired");
		}
		Date timeNow = new Date();
		if (invoice.getExpiry().before(timeNow)) {
			invoice.setStatus(InvoiceStatus.EXPIRED);
			invRepo.save(invoice);
			throw new PaycrException(Constants.FAILURE, "This invoice has expired");
		}
	}

	@RequestMapping("/decline/{invoiceCode}")
	public void decline(@PathVariable String invoiceCode, HttpServletResponse response) throws IOException {
		try {
			Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
			invoice.setStatus(InvoiceStatus.DECLINED);
			invRepo.save(invoice);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		response.sendRedirect("/response/" + invoiceCode);
	}

	@RequestMapping(value = "/return/{invoiceCode}", method = RequestMethod.POST)
	public void purchase(@RequestParam Map<String, String> formData, HttpServletResponse response) throws IOException {
		String invoiceCode = null;
		try {
			String rzpPayId = formData.get("razorpay_payment_id");
			invoiceCode = formData.get("invoiceCode");
			Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
			Merchant merchant = merRepo.findOne(invoice.getMerchant());
			for (InvoiceCustomParam param : invoice.getCustomParams()) {
				if (ParamValueProvider.CONSUMER.equals(param.getProvider())) {
					String paramValue = formData.get(param.getParamName());
					param.setParamValue(paramValue);
				}
			}
			MerchantSetting setting = merchant.getSetting();
			Payment payment = new Payment();
			payment.setCreated(new Date());
			payment.setInvoiceCode(invoiceCode);
			payment.setPaymentRefNo(rzpPayId);
			RazorpayClient razorpay = new RazorpayClient(setting.getRzpKeyId(), setting.getRzpSecretId());
			com.razorpay.Payment rzpPayment = razorpay.Payments.fetch(rzpPayId);
			JSONObject request = new JSONObject();
			request.put("amount", rzpPayment.get("amount").toString());
			if ("authorized".equals(rzpPayment.get("status"))) {
				rzpPayment = razorpay.Payments.capture(rzpPayId, request);
			}
			payment.setStatus(rzpPayment.get("status"));
			invoice.setStatus(getStatus(rzpPayment.get("status")));
			payment.setMethod(rzpPayment.get("method"));
			payment.setAmount(invoice.getPayAmount());
			payment.setPayMode(PayMode.PAYCR);
			payment.setPayType(PayType.SALE);
			payment.setBank(JSONObject.NULL.equals(rzpPayment.get("bank")) ? null : rzpPayment.get("bank"));
			payment.setWallet(JSONObject.NULL.equals(rzpPayment.get("wallet")) ? null : rzpPayment.get("wallet"));
			invoice.setPayment(payment);
			invRepo.save(invoice);
			if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
				Notification noti = new Notification();
				noti.setMerchantId(merchant.getId());
				noti.setMessage("Payment received for Invoice# " + invoiceCode);
				noti.setSubject("Invoice Paid");
				noti.setCreated(new Date());
				noti.setRead(false);
				notiRepo.save(noti);
			}
		} catch (RazorpayException e) {
			System.out.println(e.getMessage());
		}
		response.sendRedirect("/response/" + invoiceCode);
	}

	private InvoiceStatus getStatus(String rzpStatus) {
		if ("captured".equals(rzpStatus)) {
			return InvoiceStatus.PAID;
		} else {
			return InvoiceStatus.UNPAID;
		}
	}
}
