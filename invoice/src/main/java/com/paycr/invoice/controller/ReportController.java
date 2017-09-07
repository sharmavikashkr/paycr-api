package com.paycr.invoice.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.invoice.service.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ReportService repSer;

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("")
	public List<Report> getReports() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return repSer.fetchReports(merchant);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/new")
	public void createReport(@RequestBody Report report, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			report.setMerchant(merchant);
			report.setCreated(new Date());
			repSer.createReports(report);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/load")
	public List<InvoiceReport> loadReport(@RequestBody Report report, HttpServletResponse response) {
		List<InvoiceReport> invoiceReport = new ArrayList<>();
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			report.setMerchant(merchant);
			invoiceReport = repSer.loadReport(report, merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return invoiceReport;
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/delete/{reportId}")
	public void deleteReport(@PathVariable Integer reportId, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			repSer.deleteReport(reportId, merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/download")
	public void downloadReport(@RequestBody Report report, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			String csv = repSer.downloadReport(report, merchant);
			response.setContentType("text/csv");
			byte[] data = csv.getBytes();
			response.setHeader("Content-Disposition", "attachment; filename=\"report.csv\"");
			response.setContentType("text/csv;charset=utf-8");
			InputStream is = new ByteArrayInputStream(data);
			IOUtils.copy(is, response.getOutputStream());
			response.setContentLength(data.length);
			response.flushBuffer();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

}
