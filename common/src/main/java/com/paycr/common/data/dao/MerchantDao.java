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
		if (!CommonUtil.isEmpty(searchReq.getName())) {
			squery.append(" m.name LIKE :name AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			squery.append(" m.email = :email AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getMobile())) {
			squery.append(" m.mobile = :mobile AND");
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			squery.append(" m.created between :startDate AND :endDate AND");
		}
		squery.append(" m.id > 0 ORDER BY m.id DESC");

		TypedQuery<Merchant> query = em.createQuery(squery.toString(), Merchant.class);

		if (!CommonUtil.isEmpty(searchReq.getName())) {
			query.setParameter("name", "%" + searchReq.getName() + "%");
		}
		if (!CommonUtil.isEmpty(searchReq.getEmail())) {
			query.setParameter("email", searchReq.getEmail());
		}
		if (!CommonUtil.isEmpty(searchReq.getMobile())) {
			query.setParameter("mobile", searchReq.getMobile());
		}
		if (!CommonUtil.isNull(searchReq.getCreatedFrom())) {
			query.setParameter("startDate", DateUtil.getStartOfDay(searchReq.getCreatedFrom()));
			query.setParameter("endDate", DateUtil.getEndOfDay(searchReq.getCreatedTo()));
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
