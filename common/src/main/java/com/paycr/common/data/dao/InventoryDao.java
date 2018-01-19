package com.paycr.common.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.DateFilter;
import com.paycr.common.bean.search.SearchInventoryRequest;
import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Report;
import com.paycr.common.util.CommonUtil;

@Component
public class InventoryDao {

	@PersistenceContext
	private EntityManager em;

	public List<Inventory> findInventory(SearchInventoryRequest searchReq, Merchant merchant) {
		StringBuilder squery = new StringBuilder("SELECT i FROM Inventory i WHERE");
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

		TypedQuery<Inventory> query = em.createQuery(squery.toString(), Inventory.class);

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
	public List<Object[]> getInventoryReport(Report report, Merchant merchant, DateFilter dateFilter) {
		StringBuilder squery = new StringBuilder("SELECT invn.code as code,invn.name as name,invn.rate as rate,"
				+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN ii.quantity ELSE 0 END) as saleQuantity,"
				+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN ii.price ELSE 0 END) as saleAmt FROM pc_inventory invn"
				+ " JOIN pc_invoice_item ii ON ii.inventory_id = invn.id"
				+ " JOIN pc_invoice i ON ii.invoice_id = i.id"
				+ " JOIN pc_invoice_payment p ON i.invoice_code = p.invoice_code WHERE");
		if (!CommonUtil.isNull(merchant)) {
			squery.append(" i.merchant_id = :merchantId AND");
		}
		if (CommonUtil.isNotNull(report.getPayType())) {
			squery.append(" p.pay_type = :payType AND");
		}
		if (CommonUtil.isNotNull(report.getPayMode())) {
			squery.append(" p.pay_mode = :payMode AND");
		}
		squery.append(" p.paid_date BETWEEN :start AND :end AND p.status in ('captured','refund')");
		squery.append(" GROUP BY invn.code,invn.name,invn.rate;");

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