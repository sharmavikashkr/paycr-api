package com.paycr.invoice.controller;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.ChildInvoiceRequest;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.invoice.service.CreateInvoiceService;

@RestController
@RequestMapping("/invoice")
public class CreateInvoiceController {

	private static final Logger logger = LoggerFactory.getLogger(CreateInvoiceController.class);

	@Autowired
	private CreateInvoiceService crtInvSer;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public Invoice single(@RequestBody Invoice invoice, HttpServletResponse response) {
		try {
			return crtInvSer.single(invoice);
		} catch (Exception ex) {
			logger.error("Execption while creating invoice : ", ex);
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/child/{invoiceCode}", method = RequestMethod.POST)
	public Invoice createChild(@PathVariable String invoiceCode, @RequestBody ChildInvoiceRequest chldInvReq,
			HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			return crtInvSer.createChild(invoiceCode, chldInvReq, user.getEmail());
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/upload/{invoiceCode}", method = RequestMethod.POST)
	public void uploadConsumers(@PathVariable String invoiceCode, @RequestParam("consumers") MultipartFile consumers,
			HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			crtInvSer.uploadConsumers(invoiceCode, consumers, user.getEmail());
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/category/{invoiceCode}", method = RequestMethod.POST)
	public void createCategory(@PathVariable String invoiceCode, @RequestBody ChildInvoiceRequest chldInvReq,
			HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			crtInvSer.createCategory(invoiceCode, chldInvReq, user.getEmail(), merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

}
