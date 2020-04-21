package com.paycr.dashboard.service;

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
import com.paycr.common.bean.report.InvoiceReport;
import com.paycr.common.bean.search.SearchConsumerRequest;
import com.paycr.common.bean.search.SearchInventoryRequest;
import com.paycr.common.bean.search.SearchInvoicePaymentRequest;
import com.paycr.common.bean.search.SearchInvoiceRequest;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.dao.ConsumerDao;
import com.paycr.common.data.dao.InventoryDao;
import com.paycr.common.data.dao.InvoiceDao;
import com.paycr.common.data.dao.InvoicePaymentDao;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.PaycrUtil;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class InvoiceSearchService {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceSearchService.class);

	@Autowired
	private InvoiceDao invDao;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private InvoicePaymentDao invPayDao;

	@Autowired
	private ConsumerDao conDao;

	@Autowired
	private InventoryDao invenDao;

	@Autowired
	private Company company;

	@Autowired
	private Server server;

	@Autowired
	private EmailEngine emailEngine;

	public List<Invoice> fetchInvoiceList(SearchInvoiceRequest request) {
		logger.info("Search invoice request : {}", new Gson().toJson(request));
		PaycrUtil.validateRequest(request);
		PaycrUtil.validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		Merchant merchant = null;
		if (CommonUtil.isNotNull(request.getMerchant())) {
			Optional<Merchant> merOpt = merRepo.findById(request.getMerchant());
			if (merOpt.isPresent()) {
				merchant = merOpt.get();
			}
		}
		return invDao.findInvoices(request, merchant);
	}

	public List<InvoicePayment> fetchPaymentList(SearchInvoicePaymentRequest request) {
		logger.info("Search invoice payment request : {}", new Gson().toJson(request));
		PaycrUtil.validateRequest(request);
		PaycrUtil.validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		Merchant merchant = null;
		if (CommonUtil.isNotNull(request.getMerchant())) {
			Optional<Merchant> merOpt = merRepo.findById(request.getMerchant());
			if (merOpt.isPresent()) {
				merchant = merOpt.get();
			}
		}
		return invPayDao.findPayments(request, merchant);
	}

	public Set<Consumer> fetchConsumerList(SearchConsumerRequest request) {
		logger.info("Search consumer request : {}", new Gson().toJson(request));
		PaycrUtil.validateRequest(request);
		Merchant merchant = null;
		if (CommonUtil.isNotNull(request.getMerchant())) {
			Optional<Merchant> merOpt = merRepo.findById(request.getMerchant());
			if (merOpt.isPresent()) {
				merchant = merOpt.get();
			}
		}
		return conDao.findConsumers(request, merchant);
	}

	public String downloadPayments(SearchInvoicePaymentRequest request) throws IOException {
		logger.info("Download invoice payment request : {}", new Gson().toJson(request));
		List<InvoicePayment> paymentList = fetchPaymentList(request);
		List<InvoiceReport> invoiceReports = new ArrayList<>();
		for (InvoicePayment payment : paymentList) {
			Invoice invoice = invRepo.findByInvoiceCode(payment.getInvoiceCode());
			InvoiceReport invReport = new InvoiceReport();
			invReport.setPaidDate(payment.getPaidDate());
			invReport.setInvoiceCode(invoice.getInvoiceCode());
			invReport.setInvoiceStatus(invoice.getStatus());
			invReport.setPayAmount(invoice.getPayAmount());
			invReport.setAmount(payment.getAmount());
			invReport.setTax(invoice.getPayAmount().add(invoice.getDiscount()).subtract(invoice.getTotal()));
			invReport.setShipping(invoice.getShipping());
			invReport.setDiscount(invoice.getDiscount());
			invReport.setCurrency(invoice.getCurrency());
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
		records.add(new String[] { "Paid Date", "Invoice Code", "Invoice Status", "Invoice Amount", "Tax", "Shipping",
				"Discount", "Amount", "Currency", "PaymentRefNo", "Pay Type", "Pay Mode", "Pay Method", "Pay Status" });
		Iterator<InvoiceReport> it = invoiceReports.iterator();
		while (it.hasNext()) {
			InvoiceReport invr = it.next();
			records.add(new String[] { invr.getPaidDate().toString(), invr.getInvoiceCode(),
					invr.getInvoiceStatus().name(), invr.getPayAmount().toString(), invr.getTax().toString(),
					invr.getShipping().toString(), invr.getDiscount().toString(), invr.getAmount().toString(),
					invr.getCurrency().name(), invr.getPaymentRefNo(), invr.getPayType().name(),
					invr.getPayMode().name(), invr.getPayMethod(), invr.getPayStatus() });
		}
		csvWriter.writeAll(records);
		csvWriter.close();
		return writer.toString();
	}

	@Async
	@Transactional
	public void mailPayments(SearchInvoicePaymentRequest request, PcUser user) throws IOException {
		logger.info("Mail invoice payment request : {}", new Gson().toJson(request));
		Date timeNow = DateUtil.getUTCTimeInIST(new Date());
		String repCsv = downloadPayments(request);
		String fileName = "Invoice Payment - " + timeNow.getTime() + ".csv";
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
		email.setSubject("Invoice Payment Report - " + DateUtil.getDefaultDateTime(timeNow));
		email.setMessage("Invoice Payment Report - " + DateUtil.getDefaultDateTime(timeNow));
		email.setFileName(fileName);
		email.setFilePath(filePath);
		emailEngine.sendViaSES(email);
	}

	public List<Inventory> fetchInventoryList(SearchInventoryRequest request) {
		logger.info("Search inventory request : {}", new Gson().toJson(request));
		PaycrUtil.validateRequest(request);
		Merchant merchant = null;
		if (CommonUtil.isNotNull(request.getMerchant())) {
			Optional<Merchant> merOpt = merRepo.findById(request.getMerchant());
			if (merOpt.isPresent()) {
				merchant = merOpt.get();
			}
		}
		return invenDao.findInventory(request, merchant);
	}

}
