package com.paycr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import com.paycr.admin.service.StartupAdminService;
import com.paycr.admin.service.StartupPricingService;
import com.paycr.admin.service.StartupReportService;

@Component
public class ApplicationStartUp {

	@Autowired
	private StartupAdminService startupAdminService;

	@Autowired
	private StartupPricingService startupPricingService;

	@Autowired
	private StartupReportService startupReportService;

	@PostConstruct
	public void onApplicationEvent() {
		startupAdminService.createSuperAdmin();
		startupPricingService.createWelcomePricing();
		startupPricingService.createGstTaxMaster();
		startupReportService.createMasterReports();
	}

}
