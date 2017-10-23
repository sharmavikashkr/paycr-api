package com.paycr.invoice.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNotify;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.RecurringInvoice;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.RecurringInvoiceRepository;
import com.paycr.common.service.NotifyService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.RecurrType;
import com.paycr.common.util.DateUtil;
import com.paycr.invoice.helper.InvoiceHelper;

@Service
public class InvoiceSchedulerService {

	@Autowired
	private RecurringInvoiceRepository recInvRepo;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private InvoiceHelper invHelp;

	@Autowired
	private NotifyService notSer;

	@Transactional
	public void recurrInvoice() {
		Date timeNow = new Date();
		Date start = DateUtil.getStartOfDay(timeNow);
		Date end = DateUtil.getEndOfDay(timeNow);
		List<RecurringInvoice> recInvList = recInvRepo.findTodaysRecurringInvoices(start, end);
		ExecutorService exec = Executors.newFixedThreadPool(5);
		for (RecurringInvoice recInv : recInvList) {
			if (recInv.getRemaining() != 0) {
				Invoice invoice = recInv.getInvoice();
				if (InvoiceStatus.EXPIRED.equals(invoice.getStatus())) {
					continue;
				}
				Invoice childInvoice = invHelp.prepareChildInvoice(invoice.getInvoiceCode(), InvoiceType.SINGLE,
						"Scheduler");
				exec.execute(processInvoice(recInv, childInvoice, timeNow));
			}
		}
	}

	@Transactional
	public void expireInvoice() {
		Date timeNow = new Date();
		List<Invoice> expiredList = invRepo.findInvoicesToExpire(timeNow, false);
		for (Invoice expInv : expiredList) {
			expInv.setStatus(InvoiceStatus.EXPIRED);
		}
		invRepo.save(expiredList);
	}

	public Runnable processInvoice(RecurringInvoice recInv, Invoice childInvoice, Date timeNow) {
		return () -> {
			InvoiceSetting invSetting = childInvoice.getMerchant().getInvoiceSetting();
			InvoiceNotify invNot = new InvoiceNotify();
			invNot.setCreated(timeNow);
			invNot.setInvoice(childInvoice);
			invNot.setCcMe(invSetting.isCcMe());
			invNot.setCcEmail(childInvoice.getCreatedBy());
			invNot.setEmailNote(invSetting.getEmailNote());
			invNot.setEmailSubject(invSetting.getEmailSubject());
			invNot.setEmailPdf(invSetting.isEmailPdf());
			invNot.setSendEmail(invSetting.isSendEmail());
			invNot.setSendSms(invSetting.isSendSms());
			notSer.notify(childInvoice, invNot);
			ArrayList<InvoiceNotify> invNots = new ArrayList<>();
			invNots.add(invNot);
			childInvoice.setInvoiceNotices(invNots);
			invRepo.save(childInvoice);
			Date nextInvDate;
			if (RecurrType.WEEKLY.equals(recInv.getRecurr())) {
				nextInvDate = DateUtil.addDays(timeNow, 7);
			} else {
				nextInvDate = DateUtil.addDays(timeNow, 30);
			}
			recInv.setNextDate(nextInvDate);
			recInv.setRemaining(recInv.getRemaining() - 1);
			recInvRepo.save(recInv);
		};
	}

}
