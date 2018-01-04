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
import com.paycr.common.bean.ExpenseReport;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.ExpensePaymentRepository;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.util.CommonUtil;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class ExpenseReportService {

	@Autowired
	private ReportHelper repHelp;

	@Autowired
	private ExpensePaymentRepository expPayRepo;

	@Autowired
	private ExpenseRepository expRepo;

	public List<ExpenseReport> loadExpenseReport(Report report, Merchant merchant) {
		List<ExpensePayment> allExpPayments = new ArrayList<>();
		DateFilter dateFilter = repHelp.getDateFilterInIST(report.getTimeRange());
		repHelp.setDateFilterInUTC(dateFilter);
		if (merchant == null) {
			allExpPayments.addAll(expPayRepo.findPaysForAdmin(dateFilter.getStartDate(), dateFilter.getEndDate()));
		} else {
			allExpPayments.addAll(
					expPayRepo.findPaysForMerchant(merchant, dateFilter.getStartDate(), dateFilter.getEndDate()));
		}
		return prepareExpReport(report, allExpPayments);
	}

	public String getExpCsv(List<ExpenseReport> expReport) throws IOException {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Created", "Expense Code", "Expense Status", "Expense Amount", "Tax", "Discount",
				"Amount", "Currency", "PaymentRefNo", "Pay Type", "Pay Mode", "Pay Method", "Pay Status" });

		Iterator<ExpenseReport> it = expReport.iterator();
		while (it.hasNext()) {
			ExpenseReport expr = it.next();
			records.add(new String[] { expr.getCreated().toString(), expr.getExpenseCode(),
					expr.getExpenseStatus().name(), expr.getPayAmount().toString(), expr.getTax().toString(),
					expr.getDiscount().toString(), expr.getAmount().toString(), expr.getCurrency().name(),
					expr.getPaymentRefNo(), expr.getPayType().name(), expr.getPayMode().name(), expr.getPayMethod(),
					expr.getPayStatus() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

	public List<ExpenseReport> prepareExpReport(Report report, List<ExpensePayment> payments) {
		List<ExpenseReport> expReports = new ArrayList<>();
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
		for (ExpensePayment payment : payments) {
			Expense Expense = expRepo.findByExpenseCode(payment.getExpenseCode());
			ExpenseReport expReport = new ExpenseReport();
			expReport.setCreated(payment.getCreated());
			expReport.setExpenseCode(Expense.getExpenseCode());
			expReport.setExpenseStatus(Expense.getStatus());
			expReport.setPayAmount(Expense.getPayAmount());
			expReport.setAmount(payment.getAmount());
			expReport.setTax(Expense.getPayAmount().add(Expense.getDiscount()).subtract(Expense.getTotal()));
			expReport.setDiscount(Expense.getDiscount());
			expReport.setCurrency(Expense.getCurrency());
			expReport.setPaymentRefNo(payment.getPaymentRefNo());
			expReport.setPayType(payment.getPayType());
			expReport.setPayMode(payment.getPayMode());
			expReport.setPayMethod(payment.getMethod());
			expReport.setPayStatus(payment.getStatus());
			expReports.add(expReport);
		}
		return expReports;
	}

}
