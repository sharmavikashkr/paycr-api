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
import com.paycr.common.bean.InvoiceReport;
import com.paycr.common.bean.SearchConsumerRequest;
import com.paycr.common.bean.SearchInventoryRequest;
import com.paycr.common.bean.SearchInvoicePaymentRequest;
import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.bean.Server;
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
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;

import au.com.bytecode.opencsv.CSVWriter;

@Service
public class InvoiceSearchService {

	@Autowired
	private SecurityService secSer;

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
		return invDao.findInvoices(request, merchant);
	}

	public List<InvoicePayment> fetchPaymentList(SearchInvoicePaymentRequest request) {
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
		return invPayDao.findPayments(request, merchant);
	}

	public Set<Consumer> fetchConsumerList(SearchConsumerRequest request) {
		vaidateRequest(request);
		Merchant merchant = null;
		if (request.getMerchant() != null) {
			merchant = merRepo.findOne(request.getMerchant());
		}
		return conDao.findConsumers(request, merchant);
	}

	public String downloadPayments(SearchInvoicePaymentRequest request) throws IOException {
		List<InvoicePayment> paymentList = fetchPaymentList(request);
		List<InvoiceReport> invoiceReports = new ArrayList<>();
		for (InvoicePayment payment : paymentList) {
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
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ',', '\0');
		List<String[]> records = new ArrayList<>();
		records.add(new String[] { "Created", "Invoice Code", "Invoice Status", "Invoice Amount", "Tax", "Discount",
				"Amount", "Currency", "PaymentRefNo", "Pay Type", "Pay Mode", "Pay Method", "Pay Status" });
		Iterator<InvoiceReport> it = invoiceReports.iterator();
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

	public void mailPayments(SearchInvoicePaymentRequest request) throws IOException {
		PcUser user = secSer.findLoggedInUser();
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
		emailEngine.sendViaGmail(email);
	}

	public List<Inventory> fetchInventoryList(SearchInventoryRequest request) {
		vaidateRequest(request);
		Merchant merchant = null;
		if (request.getMerchant() != null) {
			merchant = merRepo.findOne(request.getMerchant());
		}
		return invenDao.findInventory(request, merchant);
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
