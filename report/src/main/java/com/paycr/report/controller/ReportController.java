package com.paycr.report.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Report;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.report.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ReportService repSer;

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@GetMapping("")
	public List<Report> getReports() {
		final Merchant merchant = secSer.getMerchantForLoggedInUser();
		final PcUser user = secSer.findLoggedInUser();
		return repSer.fetchReports(merchant, user);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@PutMapping("/new")
	public void createReport(@RequestBody final Report report) {
		final Merchant merchant = secSer.getMerchantForLoggedInUser();
		report.setMerchant(merchant);
		report.setCreated(new Date());
		repSer.createReports(report);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@PostMapping("/load")
	public List<?> loadInvoiceReport(@RequestBody final Report report) {
		final Merchant merchant = secSer.getMerchantForLoggedInUser();
		report.setMerchant(merchant);
		final List<?> reportData = repSer.loadReport(report, merchant);
		return reportData;
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@DeleteMapping("/delete/{reportId}")
	public void deleteReport(@PathVariable final Integer reportId) {
		final Merchant merchant = secSer.getMerchantForLoggedInUser();
		repSer.deleteReport(reportId, merchant);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@PostMapping("/download")
	public void downloadReport(@RequestBody final Report report, final HttpServletResponse response)
			throws IOException {
		final Merchant merchant = secSer.getMerchantForLoggedInUser();
		final String csv = repSer.downloadReport(report, merchant);
		response.setContentType("text/csv");
		final byte[] data = csv.getBytes();
		response.setHeader("Content-Disposition", "attachment; filename=\"report.csv\"");
		response.setContentType("text/csv;charset=utf-8");
		response.getOutputStream().write(data);
		response.setContentLength(data.length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@PostMapping("/mail")
	public void mailReport(@RequestBody final Report report) throws IOException {
		final Merchant merchant = secSer.getMerchantForLoggedInUser();
		final PcUser user = secSer.findLoggedInUser();
		final List<String> mailTo = new ArrayList<>();
		mailTo.add(user.getEmail());
		repSer.mailReport(report, merchant, mailTo);
	}

}
