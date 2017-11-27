package com.paycr.common.data.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.SearchConsumerRequest;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerCategory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.util.CommonUtil;

@Component
public class ConsumerDao {

	@PersistenceContext
	private EntityManager em;

	public Set<Consumer> findConsumers(SearchConsumerRequest searchReq, Merchant merchant) {
		Set<Consumer> conSet = new LinkedHashSet<>();
		if (searchReq.getConCatList().isEmpty()) {
			StringBuilder squery = new StringBuilder("SELECT c FROM Consumer c WHERE");
			if (!CommonUtil.isNull(merchant)) {
				squery.append(" c.merchant = :merchant AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				squery.append(" c.email = :email AND");
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				squery.append(" c.mobile = :mobile AND");
			}
			squery.append(" c.id > 0 ORDER BY c.id DESC");

			TypedQuery<Consumer> query = em.createQuery(squery.toString(), Consumer.class);

			if (!CommonUtil.isNull(merchant)) {
				query.setParameter("merchant", merchant);
			}
			if (!CommonUtil.isEmpty(searchReq.getEmail())) {
				query.setParameter("email", searchReq.getEmail());
			}
			if (!CommonUtil.isEmpty(searchReq.getMobile())) {
				query.setParameter("mobile", searchReq.getMobile());
			}
			conSet.addAll(query.getResultList());
		} else {
			for (ConsumerCategory concat : searchReq.getConCatList()) {
				StringBuilder squery = new StringBuilder("SELECT cc.consumer FROM ConsumerCategory cc WHERE");
				squery.append(" cc.name = :name AND");
				squery.append(" cc.value = :value AND");
				if (!CommonUtil.isNull(merchant)) {
					squery.append(" cc.consumer.merchant = :merchant AND");
				}
				if (!CommonUtil.isEmpty(searchReq.getEmail())) {
					squery.append(" cc.consumer.email = :email AND");
				}
				if (!CommonUtil.isEmpty(searchReq.getMobile())) {
					squery.append(" cc.consumer.mobile = :mobile AND");
				}
				squery.append(" cc.consumer.id > 0 ORDER BY cc.consumer.id DESC");

				TypedQuery<Consumer> query = em.createQuery(squery.toString(), Consumer.class);

				query.setParameter("name", concat.getName());
				query.setParameter("value", concat.getValue());
				if (!CommonUtil.isNull(merchant)) {
					query.setParameter("merchant", merchant);
				}
				if (!CommonUtil.isEmpty(searchReq.getEmail())) {
					query.setParameter("email", searchReq.getEmail());
				}
				if (!CommonUtil.isEmpty(searchReq.getMobile())) {
					query.setParameter("mobile", searchReq.getMobile());
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