package com.paycr.invoice.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.RecurringInvoiceRepository;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ObjectType;
import com.paycr.common.type.RecurrType;
import com.paycr.common.util.DateUtil;
import com.paycr.invoice.helper.InvoiceHelper;

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
	private NotifyService<InvoiceNotify> invNotSer;

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
				exec.execute(processInvoice(recInv, childInvoice));
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
		invRepo.save(expiredList);
	}

	@Transactional
	public Runnable processInvoice(RecurringInvoice recInv, Invoice childInvoice) {
		logger.info("Processing recurr invoice : {}", recInv.getInvoice().getInvoiceCode());
		return () -> {
			InvoiceSetting invSetting = childInvoice.getMerchant().getInvoiceSetting();
			InvoiceNotify invNot = new InvoiceNotify();
			invNot.setCreated(new Date());
			invNot.setInvoice(childInvoice);
			invNot.setCcMe(invSetting.isCcMe());
			invNot.setCcEmail(childInvoice.getCreatedBy());
			invNot.setEmailNote(invSetting.getEmailNote());
			invNot.setEmailSubject(invSetting.getEmailSubject());
			invNot.setEmailPdf(invSetting.isEmailPdf());
			invNot.setSendEmail(invSetting.isSendEmail());
			invNot.setSendSms(invSetting.isSendSms());
			invNotSer.notify(invNot);
			tlService.saveToTimeline(childInvoice.getId(), ObjectType.INVOICE, "Notification sent to consumer", true,
					"Scheduler");
			ArrayList<InvoiceNotify> invNots = new ArrayList<>();
			invNots.add(invNot);
			childInvoice.setNotices(invNots);
			if (InvoiceStatus.CREATED.equals(childInvoice.getStatus())) {
				childInvoice.setStatus(InvoiceStatus.UNPAID);
			}
			invRepo.save(childInvoice);
			Date nextDate = recInv.getNextDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nextDate);
			if (RecurrType.WEEKLY.equals(recInv.getRecurr())) {
				calendar.add(Calendar.DATE, 7);
			} else {
				calendar.add(Calendar.MONTH, 1);
			}
			calendar.setTime(DateUtil.getStartOfDay(calendar.getTime()));
			calendar.set(Calendar.HOUR_OF_DAY, 22);
			calendar.set(Calendar.MINUTE, 0);
			recInv.setNextDate(calendar.getTime());
			recInv.setRemaining(recInv.getRemaining() - 1);
			recInvRepo.save(recInv);
		};
	}

}
