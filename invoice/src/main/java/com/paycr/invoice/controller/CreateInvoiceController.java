package com.paycr.invoice.controller;

import java.io.IOException;

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

	@Autowired
	private CreateInvoiceService crtInvSer;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public Invoice single(@RequestBody Invoice invoice) {
		return crtInvSer.single(invoice);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/child/{invoiceCode}", method = RequestMethod.POST)
	public Invoice createChild(@PathVariable String invoiceCode, @RequestBody ChildInvoiceRequest chldInvReq) {
		PcUser user = secSer.findLoggedInUser();
		return crtInvSer.createChild(invoiceCode, chldInvReq, user.getEmail());
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/upload/{invoiceCode}", method = RequestMethod.POST)
	public void uploadConsumers(@PathVariable String invoiceCode, @RequestParam("consumers") MultipartFile consumers)
			throws IOException {
		PcUser user = secSer.findLoggedInUser();
		crtInvSer.uploadConsumers(invoiceCode, consumers, user.getEmail());
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/category/{invoiceCode}", method = RequestMethod.POST)
	public void createCategory(@PathVariable String invoiceCode, @RequestBody ChildInvoiceRequest chldInvReq) {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		crtInvSer.createCategory(invoiceCode, chldInvReq, user.getEmail(), merchant);
	}

}
