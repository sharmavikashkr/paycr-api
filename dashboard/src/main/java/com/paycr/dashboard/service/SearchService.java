package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.paycr.common.data.domain.Invoice;

@Service
public class SearchService {

	public List<JsonObject> parseInvoiceList(List<Invoice> invoiceList) {
		List<JsonObject> jsonList = new ArrayList<JsonObject>();
		for (Invoice inv : invoiceList) {
			JsonObject json = new JsonObject();
			json.addProperty("invoiceCode", inv.getInvoiceCode());
			json.addProperty("amount", inv.getPayAmount());
			json.addProperty("email", inv.getConsumer().getEmail());
			json.addProperty("mobile", inv.getConsumer().getMobile());

			jsonList.add(json);
		}
		return jsonList;
	}

}
