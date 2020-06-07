package com.paycr.invoice.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.paycr.common.data.domain.BulkFlag;
import com.paycr.common.data.domain.BulkInvoiceUpload;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.invoice.service.InvoiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

	@Autowired
	private InvoiceService invSer;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.ALL_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@GetMapping("/payments/{invoiceCode}")
	public List<InvoicePayment> payments(@PathVariable final String invoiceCode) {
		return invSer.payments(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@GetMapping("/expire/{invoiceCode}")
	public void expire(@PathVariable final String invoiceCode) {
		invSer.expire(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@DeleteMapping("/delete/{invoiceCode}")
	public void delete(@PathVariable final String invoiceCode) {
		invSer.delete(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@PostMapping("/notify/{invoiceCode}")
	public void notify(@PathVariable final String invoiceCode, @Valid @RequestBody final InvoiceNotify invoiceNotify) {
		invSer.notify(invoiceCode, invoiceNotify);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@GetMapping("/enquire/{invoiceCode}")
	public void enquire(@PathVariable final String invoiceCode) throws Exception {
		invSer.enquire(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@PostMapping("/refund")
	public void refund(@RequestParam(value = "amount", required = true) final BigDecimal amount,
			@RequestParam(value = "invoiceCode", required = true) final String invoiceCode) throws Exception {
		invSer.refund(amount, invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PostMapping("/note/new")
	public void newNote(@Valid @RequestBody final InvoiceNote note) throws Exception {
		invSer.newNote(note);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PostMapping("/markpaid")
	public void markPaid(@RequestBody final InvoicePayment payment) {
		invSer.markPaid(payment);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@PostMapping("/{invoiceCode}/attachment/new")
	public void addAttachment(@PathVariable final String invoiceCode,
			@RequestParam("attach") final MultipartFile attach) throws IOException {
		invSer.saveAttach(invoiceCode, attach);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@GetMapping("/{accessKey}/{invoiceCode}/attachment/{attachName:.+}")
	public void getAttachment(@PathVariable final String accessKey, @PathVariable final String invoiceCode,
			@PathVariable final String attachName, final HttpServletResponse response) throws IOException {
		final byte[] data = invSer.getAttach(accessKey, invoiceCode, attachName);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + attachName + "\"");
		response.getOutputStream().write(data);
		response.setContentLength(data.length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@PostMapping("/recurr/new/{invoiceCode}")
	public void recurr(@PathVariable final String invoiceCode, @RequestBody final RecurringInvoice recInv) {
		final PcUser user = secSer.findLoggedInUser();
		invSer.recurr(invoiceCode, recInv, user.getEmail());
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@GetMapping("/recurr/all/{invoiceCode}")
	public List<RecurringInvoice> allRecurr(@PathVariable final String invoiceCode) {
		return invSer.allRecurr(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@GetMapping("/bulk/upload/format")
	public void downloadFormat(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final String content = "Name1,Email1,Mobile1\r\nName2,Email2,Mobile2";
		response.setHeader("Content-Disposition", "attachment; filename=\"bulkInvoice.csv\"");
		response.setContentType("application/csv");
		response.getOutputStream().write(content.getBytes());
		response.setContentLength(content.getBytes().length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@GetMapping("/bulk/uploads/all/{invoiceCode}")
	public List<BulkInvoiceUpload> uploadConsumers(@PathVariable final String invoiceCode) {
		return invSer.getUploads(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission(#invoiceCode, 'INVOICE', 'invoiceCode')")
	@GetMapping("/bulk/flags/{invoiceCode}")
	public List<BulkFlag> bulkFlags(@PathVariable final String invoiceCode) {
		return invSer.getFlags(invoiceCode);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@GetMapping("/bulk/download/{accessKey}/{filename:.+}")
	public byte[] downloadFile(@PathVariable final String accessKey, @PathVariable final String filename)
			throws IOException {
		return invSer.downloadFile(accessKey, filename);
	}

}
