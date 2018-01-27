package com.paycr.report.service.gst;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr1B2CLarge;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.util.CommonUtil;
import com.paycr.report.helper.GstHelper;

@Service
public class Gstr1B2CLargeService {

	@Autowired
	private GstHelper gstHelp;

	public List<Gstr1B2CLarge> collectB2CLargeList(List<Invoice> invoiceList) {
		List<Invoice> largeInvList = invoiceList.stream()
				.filter(t -> ((BigDecimal.valueOf(250000).compareTo(t.getTotalPrice()) < 0)
						&& CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2CLarge> b2cLargeList = new ArrayList<Gstr1B2CLarge>();
		for (Invoice invoice : largeInvList) {
			Gstr1B2CLarge b2cLargeInv = new Gstr1B2CLarge();
			b2cLargeInv.setInvoiceAmount(invoice.getTotalPrice());
			b2cLargeInv.setInvoiceDate(invoice.getInvoiceDate());
			b2cLargeInv.setInvoiceNo(invoice.getInvoiceCode());
			if (CommonUtil.isNotNull(invoice.getConsumer().getShippingAddress())) {
				b2cLargeInv.setPlaceOfSupply(invoice.getConsumer().getShippingAddress().getState());
			}
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(invoice.getItems());
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

}
