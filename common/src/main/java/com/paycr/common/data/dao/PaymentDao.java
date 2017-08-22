package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.SearchPaymentRequest;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Component
public class PaymentDao {

	@PersistenceContext
	private EntityManager em;

	public List<Payment> findPayments(SearchPaymentRequest searchReq, Merchant merchant) {
		StringBuilder squery = new StringBuilder("SELECT p FROM Payment p WHERE");
		if (!CommonUtil.isNull(merchant)) {
			squery.append(" p.merchant = :merchant AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
			squery.append(" p.invoiceCode = :invoiceCode AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
			squery.append(" p.paymentRefNo = :paymentRefNo AND");
		}
		if (!CommonUtil.isNull(searchReq.getPayType())) {
			squery.append(" p.payType = :payType AND");
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			squery.append(" p.created between :startDate AND :endDate AND");
		}
		squery.append(" p.id > 0 ORDER BY p.id DESC");

		TypedQuery<Payment> query = em.createQuery(squery.toString(), Payment.class);

		if (!CommonUtil.isNull(merchant)) {
			query.setParameter("merchant", merchant);
		}
		if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
			query.setParameter("invoiceCode", searchReq.getInvoiceCode());
		}
		if (!CommonUtil.isEmpty(searchReq.getInvoiceCode())) {
			query.setParameter("paymentRefNo", searchReq.getPaymentRefNo());
		}
		if (!CommonUtil.isNull(searchReq.getPayType())) {
			query.setParameter("payType", searchReq.getPayType());
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
			query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
		}
		return query.getResultList();
	}

}
