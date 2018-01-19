package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.search.SearchSubsRequest;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Component
public class SubscriptionDao {

	@PersistenceContext
	private EntityManager em;

	public List<Subscription> findSubscriptions(SearchSubsRequest searchReq) {
		StringBuilder squery = new StringBuilder("SELECT s FROM Subscription s WHERE");
		if (!CommonUtil.isNull(searchReq.getMerchant())) {
			squery.append(" s.merchant.id = :merchant AND");
		}
		if (!CommonUtil.isNull(searchReq.getPricing())) {
			squery.append(" s.pricing.id = :pricing AND");
		}
		if (!CommonUtil.isNull(searchReq.getPayMode())) {
			squery.append(" s.payMode = :payMode AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getSubsCode())) {
			squery.append(" s.subscriptionCode = :subsCode AND");
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			squery.append(" s.created between :startDate AND :endDate AND");
		}
		squery.append(" s.id > 0 ORDER BY s.id DESC");

		TypedQuery<Subscription> query = em.createQuery(squery.toString(), Subscription.class);

		if (!CommonUtil.isNull(searchReq.getMerchant())) {
			query.setParameter("merchant", searchReq.getMerchant());
		}
		if (!CommonUtil.isNull(searchReq.getPricing())) {
			query.setParameter("pricing", searchReq.getPricing());
		}
		if (!CommonUtil.isNull(searchReq.getPayMode())) {
			query.setParameter("payMode", searchReq.getPayMode());
		}
		if (!CommonUtil.isEmpty(searchReq.getSubsCode())) {
			query.setParameter("subsCode", searchReq.getSubsCode());
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
			query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
		}
		return query.getResultList();
	}

}
