package com.paycr.common.data.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.bean.SearchInvoiceResponse;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Component
public class InvoiceDao {

	@Value("${dashboard.pageSize}")
	private int pageSize;

	@PersistenceContext
	private EntityManager em;

	public SearchInvoiceResponse findInvoices(SearchInvoiceRequest searchReq, Merchant merchant) {
		List<Invoice> invoices = null;
		StringBuilder squery = new StringBuilder("SELECT i FROM Invoice i WHERE");
		int pos = 1;
		if (!CommonUtil.isNull(merchant)) {
			squery.append(" i.merchant = ?" + pos++ + " AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
			squery.append(" i.invoiceCode = ?" + pos++ + " AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			squery.append(" i.consumer.email = ?" + pos++ + " AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getMobile())) {
			squery.append(" i.consumer.mobile = ?" + pos++ + " AND");
		}
		if (!CommonUtil.isNull(searchReq.getAmount())) {
			squery.append(" i.payAmount = ?" + pos++ + " AND");
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			squery.append(" i.created between ?" + pos++ + " AND ?" + pos++ + " AND");
		}
		squery.append(" i.id > 0 ORDER BY i.id DESC");

		TypedQuery<Invoice> query = em.createQuery(squery.toString(), Invoice.class);

		pos = 1;
		if (!CommonUtil.isNull(merchant)) {
			query.setParameter(pos++, merchant);
		}
		if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
			query.setParameter(pos++, searchReq.getInvoiceCode());
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			query.setParameter(pos++, searchReq.getEmail());
		}
		if (!CommonUtil.isEmpty(searchReq.getMobile())) {
			query.setParameter(pos++, searchReq.getMobile());
		}
		if (!CommonUtil.isNull(searchReq.getAmount())) {
			query.setParameter(pos++, searchReq.getAmount());
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			query.setParameter(pos++, DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
			query.setParameter(pos++, DateUtil.getEndOfDay(searchReq.getCreatedTo()));
		}
		int noOfInvoices = query.getResultList().size();
		query.setFirstResult(pageSize * (searchReq.getPage() - 1));
		query.setMaxResults(pageSize);
		invoices = query.getResultList();
		SearchInvoiceResponse response = new SearchInvoiceResponse();
		response.setInvoiceList(invoices);
		int noOfPages = 1;
		if (noOfInvoices % pageSize == 0) {
			noOfPages = noOfInvoices / pageSize;
		} else {
			noOfPages = noOfInvoices / pageSize + 1;
		}
		List<Integer> allPages = new ArrayList<Integer>();
		for (int i = 1; i <= noOfPages; i++) {
			allPages.add(i);
		}
		response.setAllPages(allPages);
		response.setPage(searchReq.getPage());
		return response;
	}

}
