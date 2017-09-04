package com.paycr.invoice.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.paycr.invoice.service.InvoiceSchedulerService;

@Component
public class InvoiceScheduler {

	@Autowired
	private InvoiceSchedulerService invSchSer;

	@Scheduled(cron = "0 30 3 * * ?")
	public void recurrInvoice() {
		invSchSer.recurrInvoice();
	}

}
