package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.DateFilter;
import com.paycr.common.bean.search.SearchInvoiceRequest;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Component
public class InvoiceDao {

	@PersistenceContext
	private EntityManager em;

	public List<Invoice> findInvoices(SearchInvoiceRequest searchReq, Merchant merchant) {
		if (CommonUtil.isEmpty(searchReq.getItemCode())) {
			StringBuilder squery = new StringBuilder("SELECT i FROM Invoice i WHERE");
			if (!CommonUtil.isNull(merchant)) {
				squery.append(" i.merchant = :merchant AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
				squery.append(" i.invoiceCode LIKE :invoiceCode AND");
			}
			if (!CommonUtil.isNull(searchReq.getInvoiceType())) {
				squery.append(" i.invoiceType = :invoiceType AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getParentInvoiceCode())) {
				squery.append(" i.parent.invoiceCode LIKE :parentInvoiceCode AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				squery.append(" i.consumer.email LIKE :email AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				squery.append(" i.consumer.mobile LIKE :mobile AND");
			}
			if (!CommonUtil.isNull(searchReq.getAmount())) {
				squery.append(" i.payAmount = :amount AND");
			}
			if (!CommonUtil.isNull(searchReq.getInvoiceStatus())) {
				squery.append(" i.status = :status AND");
			}
			if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
				squery.append(" i.created between :startDate AND :endDate AND");
			}
			squery.append(" i.deleted = FALSE AND i.id > 0 ORDER BY i.id DESC");

			TypedQuery<Invoice> query = em.createQuery(squery.toString(), Invoice.class);

			if (!CommonUtil.isNull(merchant)) {
				query.setParameter("merchant", merchant);
			}
			if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
				query.setParameter("invoiceCode", "%" + searchReq.getInvoiceCode() + "%");
			}
			if (!CommonUtil.isNull(searchReq.getInvoiceType())) {
				query.setParameter("invoiceType", searchReq.getInvoiceType());
			}
			if (!CommonUtil.isEmpty(searchReq.getParentInvoiceCode())) {
				query.setParameter("parentInvoiceCode", "%" + searchReq.getParentInvoiceCode() + "%");
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				query.setParameter("email", "%" + searchReq.getEmail() + "%");
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				query.setParameter("mobile", "%" + searchReq.getMobile() + "%");
			}
			if (!CommonUtil.isNull(searchReq.getAmount())) {
				query.setParameter("amount", searchReq.getAmount());
			}
			if (!CommonUtil.isNull(searchReq.getInvoiceStatus())) {
				query.setParameter("status", searchReq.getInvoiceStatus());
			}
			if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
				query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
				query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
			}
			return query.getResultList();
		} else {
			StringBuilder squery = new StringBuilder("SELECT i.invoice FROM InvoiceItem i WHERE");
			squery.append(" i.inventory.code = :itemCode AND");
			if (!CommonUtil.isNull(merchant)) {
				squery.append(" i.invoice.merchant = :merchant AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
				squery.append(" i.invoice.invoiceCode LIKE :invoiceCode AND");
			}
			if (!CommonUtil.isNull(searchReq.getInvoiceType())) {
				squery.append(" i.invoice.invoiceType = :invoiceType AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getParentInvoiceCode())) {
				squery.append(" i.invoice.parent.invoiceCode LIKE :parentInvoiceCode AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				squery.append(" i.invoice.consumer.email LIKE :email AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				squery.append(" i.invoice.consumer.mobile LIKE :mobile AND");
			}
			if (!CommonUtil.isNull(searchReq.getAmount())) {
				squery.append(" i.invoice.payAmount = :amount AND");
			}
			if (!CommonUtil.isNull(searchReq.getInvoiceStatus())) {
				squery.append(" i.invoice.status = :status AND");
			}
			if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
				squery.append(" i.invoice.created between :startDate AND :endDate AND");
			}
			squery.append(" i.invoice.deleted = FALSE AND i.invoice.id > 0 ORDER BY i.invoice.id DESC");

			TypedQuery<Invoice> query = em.createQuery(squery.toString(), Invoice.class);

			query.setParameter("itemCode", searchReq.getItemCode());
			if (!CommonUtil.isNull(merchant)) {
				query.setParameter("merchant", merchant);
			}
			if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
				query.setParameter("invoiceCode", "%" + searchReq.getInvoiceCode() + "%");
			}
			if (!CommonUtil.isNull(searchReq.getInvoiceType())) {
				query.setParameter("invoiceType", searchReq.getInvoiceType());
			}
			if (!CommonUtil.isEmpty(searchReq.getParentInvoiceCode())) {
				query.setParameter("parentInvoiceCode", "%" + searchReq.getParentInvoiceCode() + "%");
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				query.setParameter("email", "%" + searchReq.getEmail() + "%");
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				query.setParameter("mobile", "%" + searchReq.getMobile() + "%");
			}
			if (!CommonUtil.isNull(searchReq.getAmount())) {
				query.setParameter("amount", searchReq.getAmount());
			}
			if (!CommonUtil.isNull(searchReq.getInvoiceStatus())) {
				query.setParameter("status", searchReq.getInvoiceStatus());
			}
			if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
				query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
				query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
			}
			return query.getResultList();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getConsumerReport(Report report, Merchant merchant, DateFilter dateFilter) {
		StringBuilder squery = new StringBuilder("SELECT c.name as name,c.email as email,c.mobile as mobile,"
				+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN 1 ELSE 0 END) as invoices,"
				+ "SUM(CASE WHEN p.pay_type = 'REFUND' THEN 1 ELSE 0 END) as refunded,"
				+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN p.amount ELSE 0 END) as invoiceAmt,"
				+ "SUM(CASE WHEN p.pay_type = 'REFUND' THEN p.amount ELSE 0 END) as refundAmt FROM pc_invoice i"
				+ " JOIN pc_invoice_payment p ON i.invoice_code = p.invoice_code"
				+ " JOIN pc_consumer c ON i.consumer_id = c.id WHERE");
		if (!CommonUtil.isNull(merchant)) {
			squery.append(" i.merchant_id = :merchantId AND");
		}
		if (CommonUtil.isNotNull(report.getPayType())) {
			squery.append(" p.pay_type = :payType AND");
		}
		if (CommonUtil.isNotNull(report.getPayMode())) {
			squery.append(" p.pay_mode = :payMode AND");
		}
		squery.append(" i.deleted = FALSE AND p.paid_date BETWEEN :start AND :end AND p.status in ('captured','refund')");
		squery.append(" GROUP BY c.name,c.email,c.mobile;");

		Query query = em.createNativeQuery(squery.toString());

		if (!CommonUtil.isNull(merchant)) {
			query.setParameter("merchantId", merchant.getId());
		}
		if (CommonUtil.isNotNull(report.getPayType())) {
			query.setParameter("payType", report.getPayType().name());
		}
		if (CommonUtil.isNotNull(report.getPayMode())) {
			query.setParameter("payMode", report.getPayMode());
		}
		query.setParameter("start", dateFilter.getStartDate());
		query.setParameter("end", dateFilter.getEndDate());
		return query.getResultList();
	}

}
