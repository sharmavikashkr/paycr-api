package com.paycr.report.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.DateFilter;
import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.InvoicePaymentRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.util.CommonUtil;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class InvoiceReportService {

	@Autowired
	private ReportHelper repHelp;

	@Autowired
	private InvoicePaymentRepository invPayRepo;

	@Autowired
	private InvoiceRepository invRepo;

	public List<InvoiceReport> loadInvoiceReport(Report report, Merchant merchant) {
		List<InvoicePayment> allInvPayments = new ArrayList<>();
		DateFilter dateFilter = repHelp.getDateFilter(report.getTimeRange());
		if (merchant == null) {
			allInvPayments.addAll(invPayRepo.findPaysForAdmin(dateFilter.getStartDate(), dateFilter.getEndDate()));
		} else {
			allInvPayments.addAll(
					invPayRepo.findPaysForMerchant(merchant, dateFilter.getStartDate(), dateFilter.getEndDate()));
		}
		return prepareInvReport(report, allInvPayments);
	}

	public String getInvCsv(List<InvoiceReport> invReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Created", "Invoice Code", "Invoice Status", "Invoice Amount", "Tax", "Discount",
				"Amount", "Currency", "PaymentRefNo", "Pay Type", "Pay Mode", "Pay Method", "Pay Status" });

		Iterator<InvoiceReport> it = invReport.iterator();
		while (it.hasNext()) {
			InvoiceReport invr = it.next();
			records.add(new String[] { invr.getCreated().toString(), invr.getInvoiceCode(),
					invr.getInvoiceStatus().name(), invr.getPayAmount().toString(), invr.getTax().toString(),
					invr.getDiscount().toString(), invr.getAmount().toString(), invr.getCurrency().name(),
					invr.getPaymentRefNo(), invr.getPayType().name(), invr.getPayMode().name(), invr.getPayMethod(),
					invr.getPayStatus() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

	public List<InvoiceReport> prepareInvReport(Report report, List<InvoicePayment> payments) {
		List<InvoiceReport> invoiceReports = new ArrayList<>();
		payments = payments.stream()
				.filter(t -> t.getStatus().equalsIgnoreCase("captured") || t.getStatus().equalsIgnoreCase("refund"))
				.collect(Collectors.toList());
		if (CommonUtil.isNotNull(report.getPayType())) {
			payments = payments.stream().filter(t -> t.getPayType().equals(report.getPayType()))
					.collect(Collectors.toList());
		}
		if (CommonUtil.isNotNull(report.getPayType())) {
			payments = payments.stream().filter(t -> t.getPayType().equals(report.getPayType()))
					.collect(Collectors.toList());
		}
		if (CommonUtil.isNotNull(report.getPayMode())) {
			payments = payments.stream().filter(t -> t.getPayMode().equals(report.getPayMode()))
					.collect(Collectors.toList());
		}
		for (InvoicePayment payment : payments) {
			Invoice invoice = invRepo.findByInvoiceCode(payment.getInvoiceCode());
			InvoiceReport invReport = new InvoiceReport();
			invReport.setCreated(payment.getCreated());
			invReport.setInvoiceCode(invoice.getInvoiceCode());
			invReport.setInvoiceStatus(invoice.getStatus());
			invReport.setPayAmount(invoice.getPayAmount());
			invReport.setAmount(payment.getAmount());
			invReport.setTax(invoice.getPayAmount().add(invoice.getDiscount()).subtract(invoice.getTotal()));
			invReport.setDiscount(invoice.getDiscount());
			invReport.setCurrency(invoice.getCurrency());
			invReport.setPaymentRefNo(payment.getPaymentRefNo());
			invReport.setPayType(payment.getPayType());
			invReport.setPayMode(payment.getPayMode());
			invReport.setPayMethod(payment.getMethod());
			invReport.setPayStatus(payment.getStatus());
			invoiceReports.add(invReport);
		}
		return invoiceReports;
	}

}
