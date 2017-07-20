package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.paycr.common.bean.SearchMerchantRequest;
import com.paycr.common.bean.SearchMerchantResponse;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Component
public class MerchantDao {

	@Value("${dashboard.pageSize}")
	private int pageSize;

	@PersistenceContext
	private EntityManager em;

	public SearchMerchantResponse findMerchants(SearchMerchantRequest searchReq) {
		List<Merchant> merchants = null;
		StringBuilder squery = new StringBuilder("SELECT m FROM Merchant m WHERE");
		int pos = 1;
		if (!CommonUtil.isEmpty(searchReq.getName())) {
			squery.append(" m.name LIKE '%?%'" + pos++ + " AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			squery.append(" m.email = ?" + pos++ + " AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getMobile())) {
			squery.append(" m.mobile = ?" + pos++ + " AND");
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			squery.append(" m.created between ?" + pos++ + " AND ?" + pos++ + " AND");
		}
		squery.append(" m.id > 0 ORDER BY m.id DESC");

		TypedQuery<Merchant> query = em.createQuery(squery.toString(), Merchant.class);

		pos = 1;
		if (!CommonUtil.isEmpty(searchReq.getName())) {
			query.setParameter(pos++, searchReq.getName());
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			query.setParameter(pos++, searchReq.getEmail());
		}
		if (!CommonUtil.isEmpty(searchReq.getMobile())) {
			query.setParameter(pos++, searchReq.getMobile());
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			query.setParameter(pos++, DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
			query.setParameter(pos++, DateUtil.getEndOfDay(searchReq.getCreatedTo()));
		}
		int noOfMerchants = query.getResultList().size();
		query.setFirstResult(pageSize * (searchReq.getPage() - 1));
		query.setMaxResults(pageSize);
		merchants = query.getResultList();
		SearchMerchantResponse response = new SearchMerchantResponse();
		response.setMerchantList(merchants);
		response.setPage(searchReq.getPage());
		int noOfPages = 1;
		if (noOfMerchants % pageSize == 0) {
			noOfPages = noOfMerchants / pageSize;
		} else {
			noOfPages = noOfMerchants / pageSize + 1;
		}
		response.setNoOfPages(noOfPages);
		return response;
	}

}
