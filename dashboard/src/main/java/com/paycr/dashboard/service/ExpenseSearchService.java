package com.paycr.dashboard.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class ExpenseSearchService {

	@Autowired
	private SecurityService secSer;

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
		vaidateRequest(request);
		validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		Merchant merchant = null;
		if (request.getMerchant() != null) {
			merchant = merRepo.findOne(request.getMerchant());
		}
		return expDao.findExpenses(request, merchant);
	}

	public List<ExpensePayment> fetchPaymentList(SearchExpensePaymentRequest request) {
		vaidateRequest(request);
		validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		Merchant merchant = null;
		if (request.getMerchant() != null) {
			merchant = merRepo.findOne(request.getMerchant());
		}
		return expPayDao.findPayments(request, merchant);
	}

	public String downloadPayments(SearchExpensePaymentRequest request) throws IOException {
		List<ExpensePayment> paymentList = fetchPaymentList(request);
		List<ExpenseReport> invoiceReports = new ArrayList<>();
		for (ExpensePayment payment : paymentList) {
			Expense expense = expRepo.findByExpenseCode(payment.getExpenseCode());
			ExpenseReport invReport = new ExpenseReport();
			invReport.setPaidDate(payment.getPaidDate());
			invReport.setExpenseCode(expense.getExpenseCode());
			invReport.setExpenseStatus(expense.getStatus());
			invReport.setPayAmount(expense.getPayAmount());
			invReport.setAmount(payment.getAmount());
			invReport.setTax(expense.getPayAmount().add(expense.getDiscount()).subtract(expense.getTotal()));
			invReport.setDiscount(expense.getDiscount());
			invReport.setCurrency(expense.getCurrency());
			invReport.setPaymentRefNo(payment.getPaymentRefNo());
			invReport.setPayType(payment.getPayType());
			invReport.setPayMode(payment.getPayMode());
			invReport.setPayMethod(payment.getMethod());
			invReport.setPayStatus(payment.getStatus());
			invoiceReports.add(invReport);
		}
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Paid Date", "Expense Code", "Expense Status", "Expense Amount", "Tax", "Discount",
				"Amount", "Currency", "PaymentRefNo", "Pay Type", "Pay Mode", "Pay Method", "Pay Status" });
		Iterator<ExpenseReport> it = invoiceReports.iterator();
		while (it.hasNext()) {
			ExpenseReport expr = it.next();
			records.add(new String[] { expr.getPaidDate().toString(), expr.getExpenseCode(),
					expr.getExpenseStatus().name(), expr.getPayAmount().toString(), expr.getTax().toString(),
					expr.getDiscount().toString(), expr.getAmount().toString(), expr.getCurrency().name(),
					expr.getPaymentRefNo(), expr.getPayType().name(), expr.getPayMode().name(), expr.getPayMethod(),
					expr.getPayStatus() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

	public void mailPayments(SearchExpensePaymentRequest request) throws IOException {
		PcUser user = secSer.findLoggedInUser();
		Date timeNow = DateUtil.getUTCTimeInIST(new Date());
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
		emailEngine.sendViaGmail(email);
	}

	public Set<Supplier> fetchSupplierList(SearchSupplierRequest request) {
		vaidateRequest(request);
		Merchant merchant = null;
		if (request.getMerchant() != null) {
			merchant = merRepo.findOne(request.getMerchant());
		}
		return conDao.findSuppliers(request, merchant);
	}

	public List<Asset> fetchAssetList(SearchAssetRequest request) {
		vaidateRequest(request);
		Merchant merchant = null;
		if (request.getMerchant() != null) {
			merchant = merRepo.findOne(request.getMerchant());
		}
		return assetDao.findAsset(request, merchant);
	}

	private void vaidateRequest(Object request) {
		if (request == null) {
			throw new PaycrException(Constants.FAILURE, "Mandatory params missing");
		}
	}

	private void validateDates(Date from, Date to) {
		if (from == null || to == null) {
			throw new PaycrException(Constants.FAILURE, "From/To dates cannot be null");
		}
		from = DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(from)));
		to = DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(to)));
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(to);
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(from);
		calFrom.add(Calendar.DAY_OF_YEAR, 90);
		if (calFrom.before(calTo)) {
			throw new PaycrException(Constants.FAILURE, "Search duration cannot be greater than 90 days");
		}
	}

}
