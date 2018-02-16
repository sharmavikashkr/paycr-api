package com.paycr.common.data.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.search.SearchConsumerRequest;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerFlag;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.util.CommonUtil;

@Component
public class ConsumerDao {

	@PersistenceContext
	private EntityManager em;

	public Set<Consumer> findConsumers(SearchConsumerRequest searchReq, Merchant merchant) {
		Set<Consumer> conSet = new LinkedHashSet<>();
		if (searchReq.getFlagList().isEmpty()) {
			StringBuilder squery = new StringBuilder("SELECT c FROM Consumer c WHERE");
			if (!CommonUtil.isNull(merchant)) {
				squery.append(" c.merchant = :merchant AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getName())) {
				squery.append(" c.name LIKE :name AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				squery.append(" c.email LIKE :email AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				squery.append(" c.mobile LIKE :mobile AND");
			}
			squery.append(" c.id > 0 ORDER BY c.id DESC");

			TypedQuery<Consumer> query = em.createQuery(squery.toString(), Consumer.class);

			if (!CommonUtil.isNull(merchant)) {
				query.setParameter("merchant", merchant);
			}
			if (!CommonUtil.isEmpty(searchReq.getName())) {
				query.setParameter("name", "%" + searchReq.getName() + "%");
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				query.setParameter("email", "%" + searchReq.getEmail() + "%");
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				query.setParameter("mobile", "%" + searchReq.getMobile() + "%");
			}
			conSet.addAll(query.getResultList());
		} else {
			for (ConsumerFlag flag : searchReq.getFlagList()) {
				StringBuilder squery = new StringBuilder("SELECT cf.consumer FROM ConsumerFlag cf WHERE");
				squery.append(" cf.name = :name AND");
				if (!CommonUtil.isNull(merchant)) {
					squery.append(" cf.consumer.merchant = :merchant AND");
				}
				if (!CommonUtil.isEmpty(searchReq.getName())) {
					squery.append(" cf.consumer.name LIKE :name AND");
				}
				if (!CommonUtil.isEmpty(searchReq.getEmail())) {
					squery.append(" cf.consumer.email LIKE :email AND");
				}
				if (!CommonUtil.isEmpty(searchReq.getMobile())) {
					squery.append(" cf.consumer.mobile LIKE :mobile AND");
				}
				squery.append(" cf.consumer.id > 0 ORDER BY cf.consumer.id DESC");

				TypedQuery<Consumer> query = em.createQuery(squery.toString(), Consumer.class);

				query.setParameter("name", flag.getName());
				if (!CommonUtil.isNull(merchant)) {
					query.setParameter("merchant", merchant);
				}
				if (!CommonUtil.isEmpty(searchReq.getName())) {
					query.setParameter("name", "%" + searchReq.getName() + "%");
				}
				if (!CommonUtil.isEmpty(searchReq.getEmail())) {
					query.setParameter("email", "%" + searchReq.getEmail() + "%");
				}
				if (!CommonUtil.isEmpty(searchReq.getMobile())) {
					query.setParameter("mobile", "%" + searchReq.getMobile() + "%");
				}
				List<Consumer> conList = query.getResultList();
				if (conSet.isEmpty()) {
					conSet.addAll(conList);
				} else {
					List<Consumer> uniqueConList = new ArrayList<>();
					for (Consumer con : conSet) {
						if (conList.contains(con)) {
							uniqueConList.add(con);
						}
					}
					conSet.clear();
					conSet.addAll(uniqueConList);
				}
			}
		}
		return conSet;
	}

}