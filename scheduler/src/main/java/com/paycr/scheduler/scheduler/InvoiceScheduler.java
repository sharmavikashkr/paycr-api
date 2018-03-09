package com.paycr.scheduler.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.paycr.invoice.scheduler.InvoiceSchedulerService;

@Component
public class InvoiceScheduler {

	@Autowired
	private InvoiceSchedulerService invSchSer;

	@Scheduled(cron = "${recurring.invoice.cron}")
	public void recurrInvoice() {
		invSchSer.recurrInvoice();
	}

	@Scheduled(cron = "${expire.invoice.cron}")
	public void expireInvoice() {
		invSchSer.expireInvoice();
	}

	@Scheduled(cron = "${remind.invoice.cron}")
	public void remindInvoice() {
		invSchSer.remindInvoice();
	}

}
