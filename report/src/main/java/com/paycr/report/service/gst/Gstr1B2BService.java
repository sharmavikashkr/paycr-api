package com.paycr.report.service.gst;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.TaxAmount;
import com.paycr.common.bean.gst.Gstr1B2B;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.util.CommonUtil;
import com.paycr.report.helper.GstHelper;

@Service
public class Gstr1B2BService {

	@Autowired
	private GstHelper gstHelp;

	public List<Gstr1B2B> collectB2BInvList(List<Invoice> invoiceList) {
		List<Invoice> b2bInvList = invoiceList.stream().filter(t -> (!CommonUtil.isEmpty(t.getConsumer().getGstin())))
				.collect(Collectors.toList());
		List<Gstr1B2B> b2bList = new ArrayList<Gstr1B2B>();
		for (Invoice invoice : b2bInvList) {
			Gstr1B2B b2bInv = new Gstr1B2B();
			b2bInv.setGstin(invoice.getConsumer().getGstin());
			b2bInv.setInvoiceAmount(invoice.getTotalPrice());
			b2bInv.setInvoiceDate(invoice.getInvoiceDate());
			b2bInv.setInvoiceNo(invoice.getInvoiceCode());
			if (CommonUtil.isNotNull(invoice.getConsumer().getShippingAddress())) {
				b2bInv.setPlaceOfSupply(invoice.getConsumer().getShippingAddress().getState());
			}
			List<TaxAmount> taxAmtList = gstHelp.getTaxAmount(invoice);
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

}
