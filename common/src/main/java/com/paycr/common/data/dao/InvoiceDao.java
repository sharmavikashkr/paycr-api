package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Component
public class InvoiceDao {

	@PersistenceContext
	private EntityManager em;

	public List<Invoice> findInvoices(SearchInvoiceRequest searchReq) {
		List<Invoice> invoices = null;
		StringBuilder squery = new StringBuilder("SELECT i FROM Invoice i WHERE");
		int pos = 1;
		if (!CommonUtil.isNull(searchReq.getMerchant())) {
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
		if (!CommonUtil.isNull(searchReq.getMerchant())) {
			query.setParameter(pos++, searchReq.getMerchant());
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
		try {
			invoices = query.getResultList();
		} catch (Exception nre) {

		}
		return invoices;
	}

}
