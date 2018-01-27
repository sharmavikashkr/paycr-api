package com.paycr.report.service.gst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.data.domain.GstSetting;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.InvoiceNoteRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.util.DateUtil;

@Service
public class GstService {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private InvoiceNoteRepository invNoteRepo;

	@Autowired
	private Gstr1B2BService b2bSer;

	@Autowired
	private Gstr1B2CLargeService b2cLargeSer;

	@Autowired
	private Gstr1B2CSmallService b2cSmallSer;

	public Gstr1Report loadGstr1Report(Merchant merchant, String monthStr) throws Exception {
		Gstr1Report gstr1Report = new Gstr1Report();
		String month = monthStr.split("-")[0];
		String year = monthStr.split("-")[1];
		Date aDayInMonth = DateUtil.parseDefaultDate(year + "-" + month + "-15");
		Date startOfMonth = DateUtil.getFirstDayOfMonth(aDayInMonth);
		Date endOfMonth = DateUtil.getLastDayOfMonth(aDayInMonth);
		List<InvoiceStatus> gstStatuses = new ArrayList<InvoiceStatus>();
		GstSetting gstSet = merchant.getGstSetting();
		if (gstSet.isInvCreated()) {
			gstStatuses.add(InvoiceStatus.CREATED);
		}
		if (gstSet.isInvDeclined()) {
			gstStatuses.add(InvoiceStatus.DECLINED);
		}
		if (gstSet.isInvExpired()) {
			gstStatuses.add(InvoiceStatus.EXPIRED);
		}
		if (gstSet.isInvPaid()) {
			gstStatuses.add(InvoiceStatus.PAID);
		}
		if (gstSet.isInvUnpaid()) {
			gstStatuses.add(InvoiceStatus.UNPAID);
		}
		List<Invoice> invoiceList = invRepo.findInvoicesForMerchant(merchant, gstStatuses, startOfMonth, endOfMonth);
		List<InvoiceNote> noteList = invNoteRepo.findNotesForMerchant(merchant, startOfMonth, endOfMonth);
		gstr1Report.setB2cLarge(b2cLargeSer.collectB2CLargeInvList(invoiceList));
		gstr1Report.setB2cSmall(b2cSmallSer.collectB2CSmallList(invoiceList, noteList));
		gstr1Report.setB2b(b2bSer.collectB2BInvList(invoiceList));
		return gstr1Report;
	}

}
