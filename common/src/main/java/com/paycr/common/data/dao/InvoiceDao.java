package com.paycr.common.data.dao;

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
		if (!CommonUtil.isNull(merchant)) {
			squery.append(" i.merchant = :merchant AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
			squery.append(" i.invoiceCode = :invoiceCode AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			squery.append(" i.consumer.email = :email AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getMobile())) {
			squery.append(" i.consumer.mobile = :mobile AND");
		}
		if (!CommonUtil.isNull(searchReq.getAmount())) {
			squery.append(" i.payAmount = :amount AND");
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			squery.append(" i.created between :startDate AND :endDate AND");
		}
		squery.append(" i.id > 0 ORDER BY i.id DESC");

		TypedQuery<Invoice> query = em.createQuery(squery.toString(), Invoice.class);

		if (!CommonUtil.isNull(merchant)) {
			query.setParameter("merchant", merchant);
		}
		if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
			query.setParameter("invoiceCode", searchReq.getInvoiceCode());
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			query.setParameter("email", searchReq.getEmail());
		}
		if (!CommonUtil.isEmpty(searchReq.getMobile())) {
			query.setParameter("mobile", searchReq.getMobile());
		}
		if (!CommonUtil.isNull(searchReq.getAmount())) {
			query.setParameter("amount", searchReq.getAmount());
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
			query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
		}
		int noOfInvoices = query.getResultList().size();
		query.setFirstResult(pageSize * (searchReq.getPage() - 1));
		query.setMaxResults(pageSize);
		invoices = query.getResultList();
		SearchInvoiceResponse response = new SearchInvoiceResponse();
		response.setInvoiceList(invoices);
		response.setPage(searchReq.getPage());
		int noOfPages = 1;
		if (noOfInvoices % pageSize == 0) {
			noOfPages = noOfInvoices / pageSize;
		} else {
			noOfPages = noOfInvoices / pageSize + 1;
		}
		response.setNoOfPages(noOfPages);
		return response;
	}

}
