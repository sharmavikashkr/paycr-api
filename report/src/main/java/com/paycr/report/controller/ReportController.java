package com.paycr.report.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringReportUser;
import com.paycr.common.data.domain.Report;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.report.service.ReportService;

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
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		report.setMerchant(merchant);
		report.setCreated(new Date());
		repSer.createReports(report);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/load")
	public List<?> loadInvoiceReport(@RequestBody Report report, HttpServletResponse response) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		report.setMerchant(merchant);
		List<?> reportData = repSer.loadReport(report, merchant);
		return reportData;
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/delete/{reportId}")
	public void deleteReport(@PathVariable Integer reportId, HttpServletResponse response) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		repSer.deleteReport(reportId, merchant);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/download")
	public void downloadReport(@RequestBody Report report, HttpServletResponse response) throws IOException {
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
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/mail")
	public void mailReport(@RequestBody Report report, HttpServletResponse response) throws IOException {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		List<String> mailTo = new ArrayList<>();
		mailTo.add(user.getEmail());
		repSer.mailReport(report, merchant, mailTo);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/schedule/get")
	public List<RecurringReportUser> getSchedule(HttpServletResponse response) {
		PcUser user = secSer.findLoggedInUser();
		return repSer.getSchedule(user);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/schedule/add/{reportId}")
	public void addSchedule(@PathVariable Integer reportId, HttpServletResponse response) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		repSer.addSchedule(reportId, merchant, user);
	}

	@PreAuthorize(RoleUtil.ALL_OPS_AUTH)
	@RequestMapping("/schedule/remove/{recRepUserId}")
	public void removeSchedule(@PathVariable Integer recRepUserId, HttpServletResponse response) {
		PcUser user = secSer.findLoggedInUser();
		repSer.removeSchedule(recRepUserId, user);
	}

}
