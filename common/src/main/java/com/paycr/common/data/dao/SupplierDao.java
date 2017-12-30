package com.paycr.common.data.dao;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.SearchSupplierRequest;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Supplier;
import com.paycr.common.util.CommonUtil;

@Component
public class SupplierDao {

	@PersistenceContext
	private EntityManager em;

	public Set<Supplier> findSuppliers(SearchSupplierRequest searchReq, Merchant merchant) {
		Set<Supplier> conSet = new LinkedHashSet<>();
		StringBuilder squery = new StringBuilder("SELECT c FROM Supplier c WHERE");
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

		TypedQuery<Supplier> query = em.createQuery(squery.toString(), Supplier.class);

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

		return conSet;
	}

}