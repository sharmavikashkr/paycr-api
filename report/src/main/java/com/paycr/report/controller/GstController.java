package com.paycr.report.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.bean.gst.Gstr2Report;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.report.service.gst.Gstr1Service;
import com.paycr.report.service.gst.Gstr2Service;

@RestController
@RequestMapping("/gst")
public class GstController {

	@Autowired
	private Gstr1Service gstr1Ser;

	@Autowired
	private Gstr2Service gstr2Ser;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@GetMapping("/gstr1/{period}")
	public Gstr1Report gstr1(@PathVariable String period) throws Exception {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return gstr1Ser.loadGstr1Report(merchant, period);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@GetMapping("/gstr1/download/{period}")
	public void gstr1Download(@PathVariable String period, HttpServletResponse response) throws Exception {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		response.setHeader("Content-Disposition", "attachment; filename=\"GSTR1 Report - " + period + ".zip\"");
		response.setContentType("application/zip");
		byte[] content = gstr1Ser.downloadGstr1Report(merchant, period);
		response.getOutputStream().write(content);
		response.setContentLength(content.length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@GetMapping("/gstr1/mail/{period}")
	public void gstr1Mail(@PathVariable String period) throws Exception {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		gstr1Ser.mailGstr1Report(user.getEmail(), merchant, period);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@GetMapping("/gstr2/{period}")
	public Gstr2Report gstr2(@PathVariable String period) throws Exception {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return gstr2Ser.loadGstr2Report(merchant, period);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@GetMapping("/gstr2/download/{period}")
	public void gstr2Download(@PathVariable String period, HttpServletResponse response) throws Exception {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		response.setHeader("Content-Disposition", "attachment; filename=\"GSTR2 Report - " + period + ".zip\"");
		response.setContentType("application/zip");
		byte[] content = gstr2Ser.downloadGstr2Report(merchant, period);
		response.getOutputStream().write(content);
		response.setContentLength(content.length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@GetMapping("/gstr2/mail/{period}")
	public void gstr2Mail(@PathVariable String period) throws Exception {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		PcUser user = secSer.findLoggedInUser();
		gstr2Ser.mailGstr2Report(user.getEmail(), merchant, period);
	}

}
