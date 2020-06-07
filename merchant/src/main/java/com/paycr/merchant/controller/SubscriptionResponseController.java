package com.paycr.merchant.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.util.CommonUtil;
import com.paycr.merchant.service.SubscriptionResponseService;

@RestController
@RequestMapping("/subscription/response")
public class SubscriptionResponseController {

	@Autowired
	private SubscriptionResponseService subsRespSer;

	@Autowired
	private Company company;

	@GetMapping("/{subscriptionCode}")
	public ModelAndView response(@PathVariable String subscriptionCode,
			@RequestParam(value = "show", required = false) Boolean show) throws IOException {
		ModelAndView mv = new ModelAndView("html/subs-response");
		mv.addObject("staticUrl", company.getStaticUrl());
		Subscription subs = subsRespSer.getSubscriptionByCode(subscriptionCode);
		mv.addObject("subs", subs);
		show = CommonUtil.isNotNull(show) ? show : true;
		mv.addObject("show", show);
		return mv;
	}

	@GetMapping("/download/{subscriptionCode}")
	public void download(@PathVariable String subscriptionCode, HttpServletResponse response) throws IOException {
		File pdfFile = subsRespSer.downloadPdf(subscriptionCode);
		response.setContentType("application/pdf");

		FileInputStream fis = null;
		byte[] bFile = new byte[(int) pdfFile.length()];
		fis = new FileInputStream(pdfFile);
		fis.read(bFile);
		fis.close();

		response.setHeader("Content-Disposition",
				"attachment; filename=\"SubscriptionReceipt-" + subscriptionCode + ".pdf\"");
		response.setContentType("application/pdf");
		response.getOutputStream().write(bFile);
		response.setContentLength(bFile.length);
		response.flushBuffer();
	}

}
