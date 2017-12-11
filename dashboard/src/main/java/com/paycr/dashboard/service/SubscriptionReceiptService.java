package com.paycr.dashboard.service;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.AdminSetting;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.repository.AdminSettingRepository;
import com.paycr.common.data.repository.SubscriptionRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.PdfUtil;

@Service
public class SubscriptionReceiptService {

	@Autowired
	private SubscriptionRepository subsRepo;

	@Autowired
	private AdminSettingRepository adsetRepo;

	@Autowired
	private Server server;

	@Autowired
	private Company company;

	@Autowired
	private PdfUtil pdfUtil;

	public Subscription getSubscriptionByCode(String subscriptionCode) {
		Subscription subs = subsRepo.findBySubscriptionCode(subscriptionCode);
		if (subs == null) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Subscription not found");
		}
		return subs;
	}

	public File downloadPdf(String subscriptionCode) throws IOException {
		String pdfPath = server.getSubscriptionLocation() + "/receipt" + subscriptionCode + ".pdf";
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()) {
			return pdfFile;
		}
		pdfFile.createNewFile();
		pdfUtil.makePdf(company.getAppUrl() + "/subscription/receipt/" + subscriptionCode, pdfFile.getAbsolutePath());
		return pdfFile;
	}

	public ModelAndView getSubscriptionReceipt(String subscriptionCode) {
		AdminSetting adset = adsetRepo.findAll().get(0);
		Subscription subs = getSubscriptionByCode(subscriptionCode);
		ModelAndView mv = new ModelAndView("receipt/subscription");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("subs", subs);
		mv.addObject("admin", adset);
		mv.addObject("company", company);
		return mv;
	}
}
