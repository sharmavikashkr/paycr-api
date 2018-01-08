package com.paycr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.paycr.admin.service.StartupAdminService;
import com.paycr.admin.service.StartupPricingService;
import com.paycr.admin.service.StartupReportService;

@Component
public class ApplicationStartUp implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private StartupAdminService startupAdminService;

	@Autowired
	private StartupPricingService startupPricingService;

	@Autowired
	private StartupReportService startupReportService;

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		startupAdminService.createSuperAdmin();
		startupPricingService.createWelcomePricing();
		startupPricingService.createGstTaxMaster();
		startupReportService.createMasterReports();
	}

}
