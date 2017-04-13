package com.paycr.invoice.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.NotifyService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.invoice.service.PaymentService;
import com.paycr.invoice.validation.InvoiceValidator;

@RestController
@RequestMapping("/app/invoice")
public class AppInvoiceController {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private Company company;

	@Autowired
	private NotifyService notSer;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private InvoiceValidator invValidator;

	@Autowired
	private NotifyService notifyService;

	@Autowired
	private PaymentService payService;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@RequestMapping(value = "new", method = RequestMethod.POST)
	public String single(@RequestBody Invoice invoice, HttpServletResponse response,
			@RequestHeader(value = "accessKey", required = true) String accessKey,
			@RequestHeader(value = "signature", required = true) String signature) {
		try {
			Merchant merchant = merRepo.findByAccessKey(accessKey);
			String genSign = hmacSigner.signWithSecretKey(merchant.getSecretKey(), invoice.getConsumer().getEmail());
			if (!genSign.equals(signature)) {
				throw new PaycrException(Constants.FAILURE, "Signature mismatch");
			}
			invoice.setMer(merchant);
			invValidator.validate(invoice);
			invRepo.save(invoice);
			notifyService.notify(invoice);
			return "Invoice Generated : " + company.getBaseUrl() + "/" + invoice.getInvoiceCode();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			if (ex instanceof PaycrException)
				return ex.getMessage();
			else
				return "Bad Request";
		}
	}

	@RequestMapping(value = "/expire/{invoiceCode}", method = RequestMethod.GET)
	public String expire(@PathVariable String invoiceCode, HttpServletResponse response,
			@RequestHeader(value = "accessKey", required = true) String accessKey,
			@RequestHeader(value = "signature", required = true) String signature) {
		try {
			Date timeNow = new Date();
			Merchant merchant = merRepo.findByAccessKey(accessKey);
			String genSign = hmacSigner.signWithSecretKey(merchant.getSecretKey(), invoiceCode);
			if (!genSign.equals(signature)) {
				throw new PaycrException(Constants.FAILURE, "Signature mismatch");
			}
			Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant.getId());
			if (CommonUtil.isNotNull(invoice) && timeNow.compareTo(invoice.getExpiry()) < 0) {
				invoice.setExpiry(timeNow);
				invRepo.save(invoice);
				return "Invoice Expired";
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST_400);
				return "Invalid invoice or the invoice has already expired";
			}
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			if (ex instanceof PaycrException)
				return ex.getMessage();
			else
				return "Bad Request";
		}
	}

	@RequestMapping(value = "/notify/{invoiceCode}", method = RequestMethod.GET)
	public String notify(@PathVariable String invoiceCode, HttpServletResponse response,
			@RequestHeader(value = "accessKey", required = true) String accessKey,
			@RequestHeader(value = "signature", required = true) String signature) {
		try {
			Date timeNow = new Date();
			Merchant merchant = merRepo.findByAccessKey(accessKey);
			String genSign = hmacSigner.signWithSecretKey(merchant.getSecretKey(), invoiceCode);
			if (!genSign.equals(signature)) {
				throw new PaycrException(Constants.FAILURE, "Signature mismatch");
			}
			Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant.getId());
			if (CommonUtil.isNotNull(invoice) && timeNow.compareTo(invoice.getExpiry()) < 0) {
				notSer.notify(invoice);
				invRepo.save(invoice);
				return "Invoice notification sent";
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST_400);
				return "Invalid invoice or the invoice has already expired";
			}
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			if (ex instanceof PaycrException)
				return ex.getMessage();
			else
				return "Bad Request";
		}
	}

	@RequestMapping(value = "/enquire/{invoiceCode}", method = RequestMethod.GET)
	public void enquire(@PathVariable String invoiceCode, HttpServletResponse response,
			@RequestHeader(value = "accessKey", required = true) String accessKey,
			@RequestHeader(value = "signature", required = true) String signature) {
		try {
			Merchant merchant = merRepo.findByAccessKey(accessKey);
			String genSign = hmacSigner.signWithSecretKey(merchant.getSecretKey(), invoiceCode);
			if (!genSign.equals(signature)) {
				throw new PaycrException(Constants.FAILURE, "Signature mismatch");
			}
			Invoice invoice = invRepo.findByInvoiceCodeAndMerchant(invoiceCode, merchant.getId());
			if (CommonUtil.isNotNull(invoice)) {
				if (!InvoiceStatus.PAID.equals(invoice.getStatus())) {
					payService.enquire(invoice);
				}
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST_400);
			}
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
		}
	}

}
