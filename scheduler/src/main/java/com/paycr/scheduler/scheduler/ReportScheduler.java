package com.paycr.scheduler.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.paycr.scheduler.service.ReportSchedulerService;

@Component
public class ReportScheduler {

	@Autowired
	private ReportSchedulerService repSchSer;

	@Scheduled(cron = "${cron.report.scheduler}")
	public void recurrReport() {
		repSchSer.recurrReports();
	}

}
