package com.paycr.report.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr1B2B;
import com.paycr.common.bean.gst.Gstr1B2CLarge;
import com.paycr.common.bean.gst.Gstr1B2CSmall;
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

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

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
		gstr1Report.setB2cLarge(collectB2CLargeInvList(invoiceList));
		gstr1Report.setB2cSmall(collectB2CSmallList(invoiceList));
		gstr1Report.setB2b(collectB2BInvList(invoiceList));
		return gstr1Report;
	}

	private List<Gstr1B2B> collectB2BInvList(List<Invoice> invoiceList) {
		List<Invoice> b2bInvList = invoiceList.stream().filter(t -> (!CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2B> b2bList = new ArrayList<Gstr1B2B>();
		for (Invoice invoice : b2bInvList) {
			Gstr1B2B b2bInv = new Gstr1B2B();
			b2bInv.setGstin(invoice.getConsumer().getGstin());
			b2bInv.setInvoiceAmount(invoice.getPayAmount());
			b2bInv.setInvoiceDate(invoice.getInvoiceDate());
			b2bInv.setInvoiceNo(invoice.getInvoiceCode());
			if (CommonUtil.isNotNull(invoice.getConsumer().getShippingAddress())) {
				b2bInv.setPlaceOfSupply(invoice.getConsumer().getShippingAddress().getState());
			}
			List<TaxAmount> taxAmtList = getTaxAmount(invoice);
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isNull(igstList) || igstList.isEmpty()) {
				b2bInv.setSupplyType("Intra-State");
			} else {
				b2bInv.setSupplyType("Inter-State");
			}
			b2bInv.setTaxAmount(taxAmtList);
			b2bList.add(b2bInv);
		}
		return b2bList;
	}

	private List<Gstr1B2CLarge> collectB2CLargeInvList(List<Invoice> invoiceList) {
		List<Invoice> largeInvList = invoiceList.stream()
				.filter(t -> ((BigDecimal.valueOf(250000).compareTo(t.getPayAmount()) < 0)
						&& CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2CLarge> b2cLargeList = new ArrayList<Gstr1B2CLarge>();
		for (Invoice invoice : largeInvList) {
			Gstr1B2CLarge b2cLargeInv = new Gstr1B2CLarge();
			b2cLargeInv.setInvoiceAmount(invoice.getPayAmount());
			b2cLargeInv.setInvoiceDate(invoice.getInvoiceDate());
			b2cLargeInv.setInvoiceNo(invoice.getInvoiceCode());
			if (CommonUtil.isNotNull(invoice.getConsumer().getShippingAddress())) {
				b2cLargeInv.setPlaceOfSupply(invoice.getConsumer().getShippingAddress().getState());
			}
			List<TaxAmount> taxAmtList = getTaxAmount(invoice);
			List<TaxAmount> igstList = taxAmtList.stream().filter(t -> t.getTax().getName().equals("IGST"))
					.collect(Collectors.toList());
			if (CommonUtil.isNull(igstList) || igstList.isEmpty()) {
				b2cLargeInv.setSupplyType("Intra-State");
			} else {
				b2cLargeInv.setSupplyType("Inter-State");
			}
			b2cLargeInv.setTaxAmount(taxAmtList);
			b2cLargeList.add(b2cLargeInv);
		}
		return b2cLargeList;
	}

	private List<Gstr1B2CSmall> collectB2CSmallList(List<Invoice> invoiceList) {
		List<Invoice> largeInvList = invoiceList.stream()
				.filter(t -> ((BigDecimal.valueOf(250000).compareTo(t.getPayAmount()) >= 0)
						&& CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2CSmall> b2cSmallList = new ArrayList<Gstr1B2CSmall>();
		for (Invoice invoice : largeInvList) {
			List<Gstr1B2CSmall> taxbrkList = getTaxBreakup(invoice);
			for (Gstr1B2CSmall taxBrk : taxbrkList) {
				List<Gstr1B2CSmall> exstB2CSmallFt = b2cSmallList.stream()
						.filter(t -> (t.getGstRate() == taxBrk.getGstRate())).collect(Collectors.toList());
				if (CommonUtil.isNull(exstB2CSmallFt) || exstB2CSmallFt.isEmpty()) {
					b2cSmallList.add(taxBrk);
				} else {
					Gstr1B2CSmall b2cSmallInv = exstB2CSmallFt.get(0);
					b2cSmallInv.setTaxableAmount(b2cSmallInv.getTaxableAmount().add(taxBrk.getTaxableAmount()));
					b2cSmallInv.setSgstAmount(b2cSmallInv.getSgstAmount().add(taxBrk.getSgstAmount()));
					b2cSmallInv.setCgstAmount(b2cSmallInv.getCgstAmount().add(taxBrk.getCgstAmount()));
					b2cSmallInv.setIgstAmount(b2cSmallInv.getIgstAmount().add(taxBrk.getIgstAmount()));
				}
			}
		}
		return b2cSmallList;
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
					taxAmt.setAmount(BigDecimal.ZERO);
					taxes.add(taxAmt);
				}
				taxAmt.setAmount(taxAmt.getAmount()
						.add(item.getInventory().getRate().multiply(new BigDecimal(item.getQuantity()))
								.multiply(new BigDecimal(itemTax.getValue())).divide(new BigDecimal(100))
								.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}
		}
		return taxes;
	}

	private List<Gstr1B2CSmall> getTaxBreakup(Invoice invoice) {
		List<Gstr1B2CSmall> b2cSmallList = new ArrayList<>();
		for (InvoiceItem item : invoice.getItems()) {
			TaxMaster tax = item.getTax();
			Gstr1B2CSmall b2cSmallInv = null;
			List<Gstr1B2CSmall> exstB2CSmallFt = b2cSmallList.stream().filter(t -> (t.getGstRate() == tax.getValue()))
					.collect(Collectors.toList());
			if (CommonUtil.isNull(exstB2CSmallFt) || exstB2CSmallFt.isEmpty()) {
				b2cSmallInv = new Gstr1B2CSmall();
				b2cSmallInv.setGstRate(tax.getValue());
				b2cSmallInv.setCgstAmount(BigDecimal.ZERO);
				b2cSmallInv.setIgstAmount(BigDecimal.ZERO);
				b2cSmallInv.setSgstAmount(BigDecimal.ZERO);
				b2cSmallInv.setTaxableAmount(BigDecimal.ZERO);
				b2cSmallList.add(b2cSmallInv);
			} else {
				b2cSmallInv = exstB2CSmallFt.get(0);
			}
			b2cSmallInv.setTaxableAmount(b2cSmallInv.getTaxableAmount().add(item.getPrice()));
			List<TaxMaster> itemTaxes = new ArrayList<>();
			List<TaxMaster> childTaxes = taxMRepo.findByParent(tax);
			if (childTaxes == null || childTaxes.isEmpty()) {
				itemTaxes.add(tax);
			} else {
				itemTaxes.addAll(childTaxes);
			}
			for (TaxMaster itemTax : itemTaxes) {
				BigDecimal taxAmt = item.getInventory().getRate().multiply(new BigDecimal(item.getQuantity()))
						.multiply(new BigDecimal(itemTax.getValue())).divide(new BigDecimal(100))
						.setScale(2, BigDecimal.ROUND_HALF_UP);
				if (itemTax.getName().equals("SGST")) {
					b2cSmallInv.setSgstAmount(b2cSmallInv.getSgstAmount().add(taxAmt));
				} else if (itemTax.getName().equals("CGST")) {
					b2cSmallInv.setCgstAmount(b2cSmallInv.getCgstAmount().add(taxAmt));
				} else if (itemTax.getName().equals("IGST")) {
					b2cSmallInv.setIgstAmount(b2cSmallInv.getIgstAmount().add(taxAmt));
				}
			}
		}
		return b2cSmallList;
	}

}
