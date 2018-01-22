package com.paycr.report.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr1B2CLarge;
import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.data.domain.GstSetting;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Service
public class GstService {

	private static final Logger logger = LoggerFactory.getLogger(GstService.class);

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	public Gstr1Report loadGstr1Report(Merchant merchant, String monthStr) {
		Gstr1Report gstr1Report = new Gstr1Report();
		try {
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
			List<Invoice> invoiceList = invRepo.findInvoicesForMerchant(merchant, gstStatuses, startOfMonth,
					endOfMonth);
			List<Gstr1B2CLarge> b2cLarge = new ArrayList<Gstr1B2CLarge>();
			for (Invoice invoice : invoiceList) {
				Gstr1B2CLarge b2cLargeInv = new Gstr1B2CLarge();
				b2cLargeInv.setInvoiceAmount(invoice.getPayAmount());
				b2cLargeInv.setInvoiceDate(invoice.getInvoiceDate());
				b2cLargeInv.setInvoiceNo(invoice.getInvoiceCode());
				if (CommonUtil.isNotNull(invoice.getConsumer().getShippingAddress())) {
					b2cLargeInv.setPlaceOfSupply(invoice.getConsumer().getShippingAddress().getState());
				}
				b2cLargeInv.setSupplyType("Inter-State");
				b2cLargeInv.setTaxAmount(getTaxAmount(invoice));
				b2cLarge.add(b2cLargeInv);
			}
			gstr1Report.setB2cLarge(b2cLarge);
		} catch (Exception ex) {
			logger.error("Exeption occured while generating GSTR1 ", ex);
		}
		return gstr1Report;
	}

	private List<TaxAmount> getTaxAmount(Invoice invoice) {
		List<TaxAmount> taxes = new ArrayList<>();
		for (InvoiceItem item : invoice.getItems()) {
			List<TaxMaster> itemTaxes = new ArrayList<>();
			TaxMaster tax = item.getTax();
			List<TaxMaster> childTaxes = taxMRepo.findByParent(tax);
			if (childTaxes == null || childTaxes.isEmpty()) {
				itemTaxes.add(tax);
			} else {
				itemTaxes.addAll(childTaxes);
			}
			for (TaxMaster itemTax : itemTaxes) {
				TaxAmount taxAmt = null;
				for (TaxAmount taxA : taxes) {
					if (taxA.getTax().getId() == itemTax.getId()) {
						taxAmt = taxA;
						break;
					}
				}
				if (taxAmt == null) {
					taxAmt = new TaxAmount();
					taxAmt.setTax(itemTax);
					taxAmt.setAmount(new BigDecimal(0));
					taxes.add(taxAmt);
				}
				taxAmt.setAmount(item.getInventory().getRate().multiply(new BigDecimal(item.getQuantity()))
						.multiply(new BigDecimal(itemTax.getValue())).divide(new BigDecimal(100))
						.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
		}
		return taxes;
	}

}
