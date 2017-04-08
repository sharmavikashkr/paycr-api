package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.data.dao.InvoiceDao;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;

@Service
public class SearchService {

	@Autowired
	private InvoiceDao invDao;

	public List<JsonObject> fetchInvoiceList(SearchInvoiceRequest request) {
		try {
			List<Invoice> invoiceList = invDao.findInvoices(request);
			List<JsonObject> jsonList = new ArrayList<JsonObject>();
			for (Invoice inv : invoiceList) {
				JsonObject json = new JsonObject();
				json.addProperty("invoiceCode", inv.getInvoiceCode());
				json.addProperty("payAmount", inv.getPayAmount());
				json.addProperty("currency", inv.getCurrency().name());
				json.addProperty("email", inv.getConsumer().getEmail());
				json.addProperty("mobile", inv.getConsumer().getMobile());
				json.addProperty("name", inv.getConsumer().getName());
				json.addProperty("created", DateUtil.getDashboardDate(inv.getCreated()));
				json.addProperty("status", inv.getStatus().name());
				jsonList.add(json);
			}
			return jsonList;
		} catch (Exception ex) {
			throw new PaycrException(Constants.FAILURE, "Bad Request");
		}
	}

}
