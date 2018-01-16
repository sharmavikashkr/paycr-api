package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.SearchPromotionRequest;
import com.paycr.common.data.domain.Promotion;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Component
public class PromotionDao {

	@PersistenceContext
	private EntityManager em;

	public List<Promotion> findPromotions(SearchPromotionRequest searchReq) {
		StringBuilder squery = new StringBuilder("SELECT m FROM Promotion m WHERE");
		if (!CommonUtil.isEmpty(searchReq.getName())) {
			squery.append(" m.name LIKE :name AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			squery.append(" m.email = :email AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getPhone())) {
			squery.append(" m.phone = :phone AND");
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			squery.append(" m.created between :startDate AND :endDate AND");
		}
		squery.append(" m.id > 0 ORDER BY m.id DESC");

		TypedQuery<Promotion> query = em.createQuery(squery.toString(), Promotion.class);

		if (!CommonUtil.isEmpty(searchReq.getName())) {
			query.setParameter("name", "%" + searchReq.getName() + "%");
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			query.setParameter("email", searchReq.getEmail());
		}
		if (!CommonUtil.isEmpty(searchReq.getPhone())) {
			query.setParameter("phone", searchReq.getPhone());
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
			query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
		}
		return query.getResultList();
	}

}
