package com.paycr.dashboard.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.bean.SearchMerchantRequest;
import com.paycr.common.data.dao.InvoiceDao;
import com.paycr.common.data.dao.MerchantDao;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.util.Constants;

@Service
public class SearchService {

	@Autowired
	private InvoiceDao invDao;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private MerchantDao merDao;

	@Autowired
	private PaymentRepository payRepo;

	public List<Invoice> fetchInvoiceList(SearchInvoiceRequest request) {
		vaidateRequest(request);
		validateDates(request.getCreatedFrom(), request.getCreatedTo());
		Date timeNow = new Date();
		Merchant merchant = null;
		if (request.getMerchant() != null) {
			merchant = merRepo.findOne(request.getMerchant());
		}
		List<Invoice> invoiceList = invDao.findInvoices(request, merchant);
		for (Invoice invoice : invoiceList) {
			List<Payment> payments = payRepo.findByInvoiceCode(invoice.getInvoiceCode());
			invoice.setAllPayments(payments);
			if (timeNow.compareTo(invoice.getExpiry()) > 0 && !InvoiceStatus.PAID.equals(invoice.getStatus())
					&& !InvoiceStatus.EXPIRED.equals(invoice.getStatus())) {
				invoice.setStatus(InvoiceStatus.EXPIRED);
			}
		}
		invRepo.save(invoiceList);
		return invoiceList;
	}

	public List<Merchant> fetchMerchantList(SearchMerchantRequest request) {
		vaidateRequest(request);
		validateDates(request.getCreatedFrom(), request.getCreatedTo());
		return merDao.findMerchants(request);
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
