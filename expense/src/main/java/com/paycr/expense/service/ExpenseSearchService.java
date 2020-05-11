package com.paycr.expense.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.bean.report.ExpenseReport;
import com.paycr.common.bean.search.SearchAssetRequest;
import com.paycr.common.bean.search.SearchExpensePaymentRequest;
import com.paycr.common.bean.search.SearchExpenseRequest;
import com.paycr.common.bean.search.SearchSupplierRequest;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.dao.AssetDao;
import com.paycr.common.data.dao.ExpenseDao;
import com.paycr.common.data.dao.ExpensePaymentDao;
import com.paycr.common.data.dao.SupplierDao;
import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Supplier;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.PaycrUtil;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class ExpenseSearchService {

	private static final Logger logger = LoggerFactory.getLogger(ExpenseSearchService.class);

	@Autowired
	private ExpenseDao expDao;

	@Autowired
	private ExpenseRepository expRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private ExpensePaymentDao expPayDao;

	@Autowired
	private SupplierDao conDao;

	@Autowired
	private AssetDao assetDao;

	@Autowired
	private Company company;

	@Autowired
	private Server server;

	@Autowired
	private EmailEngine emailEngine;

	public List<Expense> fetchExpenseList(SearchExpenseRequest request) {
		logger.info("Search expense request : {}", new Gson().toJson(request));
		PaycrUtil.validateRequest(request);
		PaycrUtil.validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(DateUtil.getStartOfDay(request.getCreatedFrom()));
		request.setCreatedTo(DateUtil.getEndOfDay(request.getCreatedTo()));
		Merchant merchant = null;
		if (CommonUtil.isNotNull(request.getMerchant())) {
			Optional<Merchant> merOpt = merRepo.findById(request.getMerchant());
			if (merOpt.isPresent()) {
				merchant = merOpt.get();
			}
		}
		return expDao.findExpenses(request, merchant);
	}

	public List<ExpensePayment> fetchPaymentList(SearchExpensePaymentRequest request) {
		logger.info("Search expense payment request : {}", new Gson().toJson(request));
		PaycrUtil.validateRequest(request);
		PaycrUtil.validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(DateUtil.getStartOfDay(request.getCreatedFrom()));
		request.setCreatedTo(DateUtil.getEndOfDay(request.getCreatedTo()));
		Merchant merchant = null;
		if (CommonUtil.isNotNull(request.getMerchant())) {
			Optional<Merchant> merOpt = merRepo.findById(request.getMerchant());
			if (merOpt.isPresent()) {
				merchant = merOpt.get();
			}
		}
		return expPayDao.findPayments(request, merchant);
	}

	public String downloadPayments(SearchExpensePaymentRequest request) throws IOException {
		logger.info("Download expense payment request : {}", new Gson().toJson(request));
		List<ExpensePayment> paymentList = fetchPaymentList(request);
		List<ExpenseReport> invoiceReports = new ArrayList<>();
		for (ExpensePayment payment : paymentList) {
			Expense expense = expRepo.findByExpenseCode(payment.getExpenseCode());
			ExpenseReport expReport = new ExpenseReport();
			expReport.setPaidDate(payment.getPaidDate());
			expReport.setExpenseCode(expense.getExpenseCode());
			expReport.setExpenseStatus(expense.getStatus());
			expReport.setPayAmount(expense.getPayAmount());
			expReport.setAmount(payment.getAmount());
			expReport.setTax(expense.getPayAmount().add(expense.getDiscount()).subtract(expense.getTotal()));
			expReport.setShipping(expense.getShipping());
			expReport.setDiscount(expense.getDiscount());
			expReport.setCurrency(expense.getCurrency());
			expReport.setPaymentRefNo(payment.getPaymentRefNo());
			expReport.setPayType(payment.getPayType());
			expReport.setPayMode(payment.getPayMode());
			expReport.setPayMethod(payment.getMethod());
			expReport.setPayStatus(payment.getStatus());
			invoiceReports.add(expReport);
		}
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Paid Date", "Expense Code", "Expense Status", "Expense Amount", "Tax", "Shipping",
				"Discount", "Amount", "Currency", "PaymentRefNo", "Pay Type", "Pay Mode", "Pay Method", "Pay Status" });
		Iterator<ExpenseReport> it = invoiceReports.iterator();
		while (it.hasNext()) {
			ExpenseReport expr = it.next();
			records.add(new String[] { expr.getPaidDate().toString(), expr.getExpenseCode(),
					expr.getExpenseStatus().name(), expr.getPayAmount().toString(), expr.getTax().toString(),
					expr.getShipping().toString(), expr.getDiscount().toString(), expr.getAmount().toString(),
					expr.getCurrency().name(), expr.getPaymentRefNo(), expr.getPayType().name(),
					expr.getPayMode().name(), expr.getPayMethod(), expr.getPayStatus() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

	@Async
	@Transactional
	public void mailPayments(SearchExpensePaymentRequest request, PcUser user) throws IOException {
		logger.info("Mail expense payment request : {}", new Gson().toJson(request));
		Date timeNow = new Date();
		String repCsv = downloadPayments(request);
		String fileName = "Expense Payment - " + timeNow.getTime() + ".csv";
		String filePath = server.getPaymentLocation() + fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(repCsv.getBytes());
		out.close();
		List<String> to = new ArrayList<>();
		to.add(user.getEmail());
		List<String> cc = new ArrayList<>();
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		email.setSubject("Expense Payment Report - " + DateUtil.getDefaultDateTime(timeNow));
		email.setMessage("Expense Payment Report - " + DateUtil.getDefaultDateTime(timeNow));
		email.setFileName(fileName);
		email.setFilePath(filePath);
		emailEngine.sendViaSES(email);
	}

	public Set<Supplier> fetchSupplierList(SearchSupplierRequest request) {
		logger.info("Search supplier request : {}", new Gson().toJson(request));
		PaycrUtil.validateRequest(request);
		Merchant merchant = null;
		if (CommonUtil.isNotNull(request.getMerchant())) {
			Optional<Merchant> merOpt = merRepo.findById(request.getMerchant());
			if (merOpt.isPresent()) {
				merchant = merOpt.get();
			}
		}
		return conDao.findSuppliers(request, merchant);
	}

	public List<Asset> fetchAssetList(SearchAssetRequest request) {
		logger.info("Search asset request : {}", new Gson().toJson(request));
		PaycrUtil.validateRequest(request);
		Merchant merchant = null;
		if (CommonUtil.isNotNull(request.getMerchant())) {
			Optional<Merchant> merOpt = merRepo.findById(request.getMerchant());
			if (merOpt.isPresent()) {
				merchant = merOpt.get();
			}
		}
		return assetDao.findAsset(request, merchant);
	}

}
