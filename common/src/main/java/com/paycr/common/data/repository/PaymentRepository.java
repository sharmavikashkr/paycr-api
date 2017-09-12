package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	public List<Payment> findByInvoiceCode(String invoiceCode);

	public List<Payment> findByInvoiceCodeAndPayType(String invoiceCode, PayType payType);

	@Query(value = "SELECT COUNT(p) as count, SUM(p.amount) as sum FROM pc_payment p WHERE p.pay_type = ?1 AND "
			+ "p.status in ('captured','refund') AND p.created BETWEEN ?2 AND ?3", nativeQuery = true)
	public List<Object[]> findCountAndSum(String payType, Date startDate, Date endDate);

	@Query(value = "SELECT COUNT(p) as count, SUM(p.amount) as sum "
			+ "FROM pc_payment p WHERE p.merchant_id = ?1 AND p.pay_type = ?2 AND "
			+ "p.status in ('captured','refund') AND p.created BETWEEN ?3 AND ?4", nativeQuery = true)
	public List<Object[]> findCountAndSumForMerchant(Integer merchantId, String payType, Date startDate, Date endDate);

	@Query(value = "SELECT CAST(CAST(p.created as DATE) as VARCHAR(10)) as date,"
			+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN p.amount ELSE 0 END) as sale,"
			+ "SUM(CASE WHEN p.pay_type = 'REFUND' THEN p.amount ELSE 0 END) as refund "
			+ "FROM pc_payment p WHERE p.created BETWEEN ?1 AND ?2 AND p.status in ('captured','refund') "
			+ "GROUP BY CAST(p.created as DATE);", nativeQuery = true)
	public List<Object[]> findDailyPayList(Date startDate, Date endDate);

	@Query(value = "SELECT CAST(CAST(p.created as DATE) as VARCHAR(10)) as date,"
			+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN p.amount ELSE 0 END) as sale,"
			+ "SUM(CASE WHEN p.pay_type = 'REFUND' THEN p.amount ELSE 0 END) as refund "
			+ "FROM pc_payment p WHERE p.merchant_id = ?3 AND p.created BETWEEN ?1 AND ?2 AND p.status in ('captured','refund') "
			+ "GROUP BY CAST(p.created as DATE);", nativeQuery = true)
	public List<Object[]> findDailyPayListForMerchant(Date startDate, Date endDate, Integer merchantId);

	@Query("SELECT p from Payment p WHERE p.payMode = ?1 AND p.payType = ?2 AND p.created BETWEEN ?3 AND ?4")
	public List<Payment> findPaysWithMode(PayMode payMode, PayType payType, Date startDate, Date endDate);

	@Query("SELECT p from Payment p WHERE p.payMode = ?1 AND p.payType = ?2 AND p.merchant = ?3 AND p.created BETWEEN ?4 AND ?5")
	public List<Payment> findPaysWithModeForMerchant(PayMode payMode, PayType payType, Merchant merchant,
			Date startDate, Date endDate);

}
