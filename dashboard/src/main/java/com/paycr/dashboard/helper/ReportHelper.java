package com.paycr.dashboard.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Item;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.PaymentRepository;

@Component
public class ReportHelper {

	@Autowired
	private PaymentRepository payRepo;

	@Async
	public Future<List<InvoiceReport>> prepareReport(Report report, Invoice invoice) {
		List<InvoiceReport> invoiceReports = new ArrayList<InvoiceReport>();
		List<Payment> payments = payRepo.findByInvoiceCodeAndPayType(invoice.getInvoiceCode(), report.getPayType());
		BigDecimal orgAmount = new BigDecimal(0);
		for (Item item : invoice.getItems()) {
			orgAmount = orgAmount.add(item.getPrice());
		}
		for (Payment payment : payments) {
			if (payment.getPayMode().equals(report.getPayMode()) && payment.getPayType().equals(report.getPayType())) {
				InvoiceReport invReport = new InvoiceReport();
				invReport.setCreated(invoice.getCreated());
				invReport.setInvoiceCode(invoice.getInvoiceCode());
				invReport.setInvoiceStatus(invoice.getStatus());
				invReport.setPayAmount(invoice.getPayAmount());
				invReport.setTax(invoice.getPayAmount().subtract(orgAmount));
				invReport.setDiscount(invoice.getDiscount());
				invReport.setCurrency(invoice.getCurrency());
				invReport.setPaymentRefNo(payment.getPaymentRefNo());
				invReport.setPayType(payment.getPayType());
				invReport.setPayMode(payment.getPayMode());
				invReport.setPayMethod(payment.getMethod());
				invoiceReports.add(invReport);
			}
		}
		return new AsyncResult<List<InvoiceReport>>(invoiceReports);
	}

}
