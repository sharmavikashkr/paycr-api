package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.search.SearchExpensePaymentRequest;
import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Component
public class ExpensePaymentDao {

	@PersistenceContext
	private EntityManager em;

	public List<ExpensePayment> findPayments(SearchExpensePaymentRequest searchReq, Merchant merchant) {
		StringBuilder squery = new StringBuilder("SELECT p FROM ExpensePayment p WHERE");
		if (!CommonUtil.isNull(merchant)) {
			squery.append(" p.merchant = :merchant AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getExpenseCode())) {
			squery.append(" p.expenseCode LIKE :expenseCode AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getExpenseCode())) {
			squery.append(" p.paymentRefNo LIKE :paymentRefNo AND");
		}
		if (!CommonUtil.isNull(searchReq.getPayType())) {
			squery.append(" p.payType = :payType AND");
		}
		if (!CommonUtil.isNull(searchReq.getPayType())) {
			squery.append(" p.payMode = :payMode AND");
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			squery.append(" p.created between :startDate AND :endDate AND");
		}
		squery.append(" p.id > 0 ORDER BY p.id DESC");

		TypedQuery<ExpensePayment> query = em.createQuery(squery.toString(), ExpensePayment.class);

		if (!CommonUtil.isNull(merchant)) {
			query.setParameter("merchant", merchant);
		}
		if (!CommonUtil.isEmpty(searchReq.getExpenseCode())) {
			query.setParameter("expenseCode", "%" + searchReq.getExpenseCode() + "%");
		}
		if (!CommonUtil.isEmpty(searchReq.getExpenseCode())) {
			query.setParameter("paymentRefNo", "%" + searchReq.getPaymentRefNo() + "%");
		}
		if (!CommonUtil.isNull(searchReq.getPayType())) {
			query.setParameter("payType", searchReq.getPayType());
		}
		if (!CommonUtil.isNull(searchReq.getPayType())) {
			query.setParameter("payMode", searchReq.getPayMode());
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
			query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
		}
		return query.getResultList();
	}

}
