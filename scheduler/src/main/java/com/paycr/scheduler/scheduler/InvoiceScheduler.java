package com.paycr.scheduler.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.paycr.scheduler.service.InvoiceSchedulerService;

@Component
public class InvoiceScheduler {

	@Autowired
	private InvoiceSchedulerService invSchSer;

	@Scheduled(cron = "${cron.invoice.recurring}")
	public void recurrInvoice() {
		invSchSer.recurrInvoice();
	}

	@Scheduled(cron = "${cron.invoice.expire}")
	public void expireInvoice() {
		invSchSer.expireInvoice();
	}

	@Scheduled(cron = "${cron.invoice.remind}")
	public void remindInvoice() {
		invSchSer.remindInvoice();
	}

}
