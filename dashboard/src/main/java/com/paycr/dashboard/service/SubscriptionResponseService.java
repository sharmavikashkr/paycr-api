package com.paycr.dashboard.service;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.repository.SubscriptionRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.PdfUtil;

@Service
public class SubscriptionResponseService {

	@Autowired
	private SubscriptionRepository subsRepo;

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
		String pdfPath = server.getSubscriptionLocation() + "/response" + subscriptionCode + ".pdf";
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()) {
			return pdfFile;
		}
		pdfFile.createNewFile();
		pdfUtil.makePdf(company.getAppUrl() + "/subscription/response/" + subscriptionCode + "?show=false",
				pdfFile.getAbsolutePath());
		return pdfFile;
	}
}
