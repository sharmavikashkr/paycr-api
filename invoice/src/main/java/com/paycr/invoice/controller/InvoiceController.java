package com.paycr.invoice.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.data.domain.BulkCategory;
import com.paycr.common.data.domain.BulkInvoiceUpload;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.invoice.service.InvoiceService;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

	@Autowired
	private InvoiceService invSer;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping(value = "/payments/{invoiceCode}", method = RequestMethod.GET)
	public List<InvoicePayment> payments(@PathVariable String invoiceCode) {
		return invSer.payments(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/expire/{invoiceCode}", method = RequestMethod.GET)
	public void expire(@PathVariable String invoiceCode) {
		invSer.expire(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/notify/{invoiceCode}", method = RequestMethod.POST)
	public void notify(@PathVariable String invoiceCode, @Valid @RequestBody InvoiceNotify invoiceNotify) {
		invSer.notify(invoiceCode, invoiceNotify);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping(value = "/enquire/{invoiceCode}", method = RequestMethod.GET)
	public void enquire(@PathVariable String invoiceCode) throws Exception {
		invSer.enquire(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public void refund(@RequestParam(value = "amount", required = true) BigDecimal amount,
			@RequestParam(value = "invoiceCode", required = true) String invoiceCode) throws Exception {
		invSer.refund(amount, invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/note/new", method = RequestMethod.POST)
	public void newNote(@Valid @RequestBody InvoiceNote note) throws Exception {
		invSer.newNote(note);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/markpaid", method = RequestMethod.POST)
	public void markPaid(@RequestBody InvoicePayment payment) {
		invSer.markPaid(payment);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping(value = "/{invoiceCode}/attachment/new", method = RequestMethod.POST)
	public void addAttachment(@PathVariable String invoiceCode, @RequestParam("attach") MultipartFile attach)
			throws IOException {
		invSer.saveAttach(invoiceCode, attach);
	}

	@RequestMapping(value = "/{accessKey}/{invoiceCode}/attachment/{attachName:.+}", method = RequestMethod.GET)
	public void getAttachment(@PathVariable String accessKey, @PathVariable String invoiceCode,
			@PathVariable String attachName, HttpServletResponse response) throws IOException {
		byte[] data = invSer.getAttach(accessKey, invoiceCode, attachName);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + attachName + "\"");
		response.getOutputStream().write(data);
		response.setContentLength(data.length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/recurr/new/{invoiceCode}", method = RequestMethod.POST)
	public void recurr(@PathVariable String invoiceCode, @RequestBody RecurringInvoice recInv) {
		PcUser user = secSer.findLoggedInUser();
		invSer.recurr(invoiceCode, recInv, user.getEmail());
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/recurr/all/{invoiceCode}", method = RequestMethod.GET)
	public List<RecurringInvoice> allRecurr(@PathVariable String invoiceCode) {
		return invSer.allRecurr(invoiceCode);
	}

	@RequestMapping("/bulk/upload/format")
	public void downloadFormat(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "Name1,Email1,Mobile1\r\nName2,Email2,Mobile2";
		response.setHeader("Content-Disposition", "attachment; filename=\"bulkInvoice.csv\"");
		response.setContentType("application/csv");
		response.getOutputStream().write(content.getBytes());
		response.setContentLength(content.getBytes().length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/uploads/all/{invoiceCode}", method = RequestMethod.GET)
	public List<BulkInvoiceUpload> uploadConsumers(@PathVariable String invoiceCode) {
		return invSer.getUploads(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/categories/{invoiceCode}", method = RequestMethod.GET)
	public List<BulkCategory> categoryConsumers(@PathVariable String invoiceCode) {
		return invSer.getCategories(invoiceCode);
	}

	@RequestMapping(value = "/bulk/download/{accessKey}/{filename:.+}", method = RequestMethod.GET)
	public byte[] downloadFile(@PathVariable String accessKey, @PathVariable String filename) throws IOException {
		return invSer.downloadFile(accessKey, filename);
	}

}
