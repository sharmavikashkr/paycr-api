package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.SearchExpenseRequest;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Component
public class ExpenseDao {

	@PersistenceContext
	private EntityManager em;

	public List<Expense> findExpenses(SearchExpenseRequest searchReq, Merchant merchant) {
		if (CommonUtil.isEmpty(searchReq.getItemCode())) {
			StringBuilder squery = new StringBuilder("SELECT i FROM Expense i WHERE");
			if (!CommonUtil.isNull(merchant)) {
				squery.append(" i.merchant = :merchant AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getExpenseCode())) {
				squery.append(" i.expenseCode = :expenseCode AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getOrderId())) {
				squery.append(" i.orderId = :orderId AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				squery.append(" i.supplier.email = :email AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				squery.append(" i.supplier.mobile = :mobile AND");
			}
			if (!CommonUtil.isNull(searchReq.getAmount())) {
				squery.append(" i.payAmount = :amount AND");
			}
			if (!CommonUtil.isNull(searchReq.getExpenseStatus())) {
				squery.append(" i.status = :status AND");
			}
			if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
				squery.append(" i.created between :startDate AND :endDate AND");
			}
			squery.append(" i.id > 0 ORDER BY i.id DESC");

			TypedQuery<Expense> query = em.createQuery(squery.toString(), Expense.class);

			if (!CommonUtil.isNull(merchant)) {
				query.setParameter("merchant", merchant);
			}
			if (!CommonUtil.isEmpty(searchReq.getExpenseCode())) {
				query.setParameter("expenseCode", searchReq.getExpenseCode());
			}
			if (!CommonUtil.isEmpty(searchReq.getOrderId())) {
				query.setParameter("orderId", searchReq.getOrderId());
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
			if (!CommonUtil.isNull(searchReq.getExpenseStatus())) {
				query.setParameter("status", searchReq.getExpenseStatus());
			}
			if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
				query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
				query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
			}
			return query.getResultList();
		} else {
			StringBuilder squery = new StringBuilder("SELECT i.expense FROM ExpenseItem i WHERE");
			squery.append(" i.asset.code = :itemCode AND");
			if (!CommonUtil.isNull(merchant)) {
				squery.append(" i.expense.merchant = :merchant AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getExpenseCode())) {
				squery.append(" i.expense.expenseCode = :expenseCode AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getOrderId())) {
				squery.append(" i.orderId = :orderId AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				squery.append(" i.expense.supplier.email = :email AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				squery.append(" i.expense.supplier.mobile = :mobile AND");
			}
			if (!CommonUtil.isNull(searchReq.getAmount())) {
				squery.append(" i.expense.payAmount = :amount AND");
			}
			if (!CommonUtil.isNull(searchReq.getExpenseStatus())) {
				squery.append(" i.expense.status = :status AND");
			}
			if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
				squery.append(" i.expense.created between :startDate AND :endDate AND");
			}
			squery.append(" i.expense.id > 0 ORDER BY i.expense.id DESC");

			TypedQuery<Expense> query = em.createQuery(squery.toString(), Expense.class);

			query.setParameter("itemCode", searchReq.getItemCode());
			if (!CommonUtil.isNull(merchant)) {
				query.setParameter("merchant", merchant);
			}
			if (!CommonUtil.isEmpty(searchReq.getExpenseCode())) {
				query.setParameter("expenseCode", searchReq.getExpenseCode());
			}
			if (!CommonUtil.isEmpty(searchReq.getOrderId())) {
				query.setParameter("orderId", searchReq.getOrderId());
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
			if (!CommonUtil.isNull(searchReq.getExpenseStatus())) {
				query.setParameter("status", searchReq.getExpenseStatus());
			}
			if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
				query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
				query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
			}
			return query.getResultList();
		}
	}

}
