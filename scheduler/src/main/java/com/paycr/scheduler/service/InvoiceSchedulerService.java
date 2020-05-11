package com.paycr.scheduler.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.RecurringInvoiceRepository;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ObjectType;
import com.paycr.common.util.DateUtil;
import com.paycr.invoice.helper.InvoiceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceSchedulerService {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceSchedulerService.class);

	@Autowired
	private RecurringInvoiceRepository recInvRepo;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private InvoiceHelper invHelp;

	@Autowired
	private TimelineService tlService;

	private final ExecutorService exec = Executors.newFixedThreadPool(5);

	@Transactional
	public void recurrInvoice() {
		logger.info("Recurr Invoice scheduler started");
		Date timeNow = new Date();
		Date start = DateUtil.getStartOfDay(timeNow);
		Date end = DateUtil.getEndOfDay(timeNow);
		List<RecurringInvoice> recInvList = recInvRepo.findTodaysRecurringInvoices(start, end);
		for (RecurringInvoice recInv : recInvList) {
			if (recInv.getRemaining() != 0) {
				Invoice invoice = recInv.getInvoice();
				if (InvoiceStatus.EXPIRED.equals(invoice.getStatus())) {
					continue;
				}
				Invoice childInvoice = invHelp.prepareChildInvoice(invoice.getInvoiceCode(), InvoiceType.SINGLE,
						"Scheduler");
				exec.execute(invHelp.processInvoice(recInv, childInvoice));
			}
		}
		logger.info("Recurr Invoice scheduler ended");
	}

	@Transactional
	public void expireInvoice() {
		logger.info("Expire invoice scheduler started");
		Date timeNow = new Date();
		List<Invoice> expiredList = invRepo.findInvoicesToExpire(timeNow);
		for (Invoice expInv : expiredList) {
			logger.info("Expiring invoice : {}", expInv.getInvoiceCode());
			expInv.setStatus(InvoiceStatus.EXPIRED);
			tlService.saveToTimeline(expInv.getId(), ObjectType.INVOICE, "Invoice expired", true, "Scheduler");
		}
		invRepo.saveAll(expiredList);
	}

	@Transactional
	public void remindInvoice() {
		logger.info("Remind invoice scheduler started");
		Date timeNow = new Date();
		Date yesterday = DateUtil.addDays(timeNow, -1);
		List<InvoiceNotify> lastNotify = invRepo.findLastNotifies(yesterday);
		for (InvoiceNotify inf : lastNotify) {
			Invoice invoice = inf.getInvoice();
			Merchant merchant = invoice.getMerchant();
			if (DateUtil.addDays(inf.getCreated(), merchant.getInvoiceSetting().getRemindDays()).before(timeNow)) {
				exec.execute(sendReminder(invoice));
			}
		}
	}

	@Transactional
	public Runnable sendReminder(Invoice invoice) {
		return () -> {
			logger.info("Sending reminder for invoice : {}", invoice.getInvoiceCode());
			invHelp.notifyInvoice(invoice);
		};
	}

}
