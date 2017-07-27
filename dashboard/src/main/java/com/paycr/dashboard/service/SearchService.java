package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.bean.SearchInvoiceResponse;
import com.paycr.common.bean.SearchMerchantRequest;
import com.paycr.common.bean.SearchMerchantResponse;
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

	public SearchInvoiceResponse fetchInvoiceList(SearchInvoiceRequest request) {
		vaidateRequest(request);
		validateDates(request.getCreatedFrom(), request.getCreatedTo());
		Date timeNow = new Date();
		Merchant merchant = null;
		if (request.getMerchant() != null) {
			merchant = merRepo.findOne(request.getMerchant());
		}
		SearchInvoiceResponse response = invDao.findInvoicesInPage(request, merchant);
		List<Integer> allPages = new ArrayList<Integer>();
		for (int i = 1; i <= response.getNoOfPages(); i++) {
			allPages.add(i);
		}
		response.setAllPages(allPages);
		List<Invoice> invoiceList = response.getInvoiceList();
		for (Invoice invoice : invoiceList) {
			List<Payment> payments = payRepo.findByInvoiceCode(invoice.getInvoiceCode());
			invoice.setAllPayments(payments);
			if (timeNow.compareTo(invoice.getExpiry()) > 0 && !InvoiceStatus.PAID.equals(invoice.getStatus())
					&& !InvoiceStatus.EXPIRED.equals(invoice.getStatus())) {
				invoice.setStatus(InvoiceStatus.EXPIRED);
			}
		}
		invRepo.save(invoiceList);
		return response;
	}

	public SearchMerchantResponse fetchMerchantList(SearchMerchantRequest request) {
		vaidateRequest(request);
		validateDates(request.getCreatedFrom(), request.getCreatedTo());
		SearchMerchantResponse response = merDao.findMerchants(request);
		List<Integer> allPages = new ArrayList<Integer>();
		for (int i = 1; i <= response.getNoOfPages(); i++) {
			allPages.add(i);
		}
		response.setAllPages(allPages);
		return response;
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
		calTo.setTime(from);
		calFrom.add(Calendar.DAY_OF_YEAR, 60);
		if (calFrom.before(calTo)) {
			throw new PaycrException(Constants.FAILURE, "Search duration cannot be greater than 60 days");
		}
	}

}
