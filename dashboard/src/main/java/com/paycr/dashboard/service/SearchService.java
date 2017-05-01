package com.paycr.dashboard.service;

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
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;

@Service
public class SearchService {

	@Autowired
	private InvoiceDao invDao;

	@Autowired
	private PaymentRepository payRepo;

	@Autowired
	private MerchantDao merDao;

	public List<Invoice> fetchInvoiceList(SearchInvoiceRequest request) {
		try {
			List<Invoice> invoiceList = invDao.findInvoices(request);
			for (Invoice invoice : invoiceList) {
				List<Payment> payments = payRepo.findByInvoiceCode(invoice.getInvoiceCode());
				invoice.setAllPayments(payments);
			}
			return invoiceList;
		} catch (Exception ex) {
			throw new PaycrException(Constants.FAILURE, "Bad Request");
		}
	}

	public List<Merchant> fetchMerchantList(SearchMerchantRequest request) {
		try {
			List<Merchant> merchantList = merDao.findMerchants(request);
			return merchantList;
		} catch (Exception ex) {
			throw new PaycrException(Constants.FAILURE, "Bad Request");
		}
	}

}
