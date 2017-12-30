package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.SearchAssetRequest;
import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.util.CommonUtil;

@Component
public class AssetDao {

	@PersistenceContext
	private EntityManager em;

	public List<Asset> findAsset(SearchAssetRequest searchReq, Merchant merchant) {
		StringBuilder squery = new StringBuilder("SELECT i FROM Asset i WHERE");
		if (!CommonUtil.isNull(merchant)) {
			squery.append(" i.merchant = :merchant AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getCode())) {
			squery.append(" i.code = :code AND");
		}
		if (!CommonUtil.isEmpty(searchReq.getName())) {
			squery.append(" i.name = :name AND");
		}
		squery.append(" i.id > 0 ORDER BY i.id DESC");

		TypedQuery<Asset> query = em.createQuery(squery.toString(), Asset.class);

		if (!CommonUtil.isNull(merchant)) {
			query.setParameter("merchant", merchant);
		}
		if (!CommonUtil.isEmpty(searchReq.getCode())) {
			query.setParameter("code", searchReq.getCode());
		}
		if (!CommonUtil.isEmpty(searchReq.getName())) {
			query.setParameter("name", searchReq.getName());
		}
		return query.getResultList();
	}

}