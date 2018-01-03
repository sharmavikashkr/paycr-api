package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.DateFilter;
import com.paycr.common.bean.SearchAssetRequest;
import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
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

	@SuppressWarnings("unchecked")
	public List<Object[]> getAssetReport(Report report, Merchant merchant, DateFilter dateFilter) {
		StringBuilder squery = new StringBuilder("SELECT ast.code as code,ast.name as name,ast.rate as rate,"
				+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN ei.quantity ELSE 0 END) as saleQuantity,"
				+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN ei.price ELSE 0 END) as saleAmt FROM pc_asset ast"
				+ " JOIN pc_expense_item ei ON ei.asset_id = ast.id"
				+ " JOIN pc_expense i ON ei.expense_id = i.id"
				+ " JOIN pc_expense_payment p ON i.expense_code = p.expense_code WHERE");
		if (!CommonUtil.isNull(merchant)) {
			squery.append(" i.merchant_id = :merchantId AND");
		}
		if (CommonUtil.isNotNull(report.getPayType())) {
			squery.append(" p.pay_type = :payType AND");
		}
		if (CommonUtil.isNotNull(report.getPayMode())) {
			squery.append(" p.pay_mode = :payMode AND");
		}
		squery.append(" p.created BETWEEN :start AND :end AND p.status in ('captured','refund')");
		squery.append(" GROUP BY ast.code,ast.name,ast.rate;");

		Query query = em.createNativeQuery(squery.toString());

		if (!CommonUtil.isNull(merchant)) {
			query.setParameter("merchantId", merchant.getId());
		}
		if (CommonUtil.isNotNull(report.getPayType())) {
			query.setParameter("payType", report.getPayType().name());
		}
		if (CommonUtil.isNotNull(report.getPayMode())) {
			query.setParameter("payMode", report.getPayMode());
		}
		query.setParameter("start", dateFilter.getStartDate());
		query.setParameter("end", dateFilter.getEndDate());
		return query.getResultList();
	}

}