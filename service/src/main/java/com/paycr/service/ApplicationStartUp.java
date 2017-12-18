package com.paycr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.paycr.admin.service.StartupService;

@Component
public class ApplicationStartUp implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private StartupService startupService;

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		startupService.createSuperAdmin();
		startupService.createWelcomePricing();
	}

}
