package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.type.PayType;

@Repository
public interface InvoicePaymentRepository extends JpaRepository<InvoicePayment, Integer> {

	@Query("SELECT p from InvoicePayment p WHERE p.deleted = FALSE")
	public List<InvoicePayment> findByInvoiceCode(String invoiceCode);

	@Query("SELECT p from InvoicePayment p WHERE p.invoiceCode = ?1 AND p.payType = ?2 AND p.deleted = FALSE")
	public List<InvoicePayment> findByInvoiceCodeAndPayType(String invoiceCode, PayType payType);

	@Query(value = "SELECT COUNT(p) as count, SUM(p.amount) as sum FROM pc_invoice_payment p WHERE p.pay_type = ?1 AND "
			+ "p.status in ('captured','refund') AND p.deleted = FALSE AND p.paid_date BETWEEN ?2 AND ?3", nativeQuery = true)
	public List<Object[]> findCountAndSum(String payType, Date startDate, Date endDate);

	@Query(value = "SELECT COUNT(p) as count, SUM(p.amount) as sum "
			+ "FROM pc_invoice_payment p WHERE p.merchant_id = ?1 AND p.pay_type = ?2 AND "
			+ "p.status in ('captured','refund') AND p.deleted = FALSE AND p.paid_date BETWEEN ?3 AND ?4", nativeQuery = true)
	public List<Object[]> findCountAndSumForMerchant(Integer merchantId, String payType, Date startDate, Date endDate);

	@Query(value = "SELECT CAST(CAST(p.paid_date as DATE) as VARCHAR(10)) as date,"
			+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN p.amount ELSE 0 END) as sale,"
			+ "SUM(CASE WHEN p.pay_type = 'REFUND' THEN p.amount ELSE 0 END) as refund "
			+ "FROM pc_invoice_payment p WHERE p.paid_date BETWEEN ?1 AND ?2 AND p.deleted = FALSE AND p.status in ('captured','refund') "
			+ "GROUP BY CAST(p.paid_date as DATE) ORDER BY CAST(p.paid_date as DATE);", nativeQuery = true)
	public List<Object[]> findDailyPayList(Date startDate, Date endDate);

	@Query(value = "SELECT CAST(CAST(p.paid_date as DATE) as VARCHAR(10)) as date,"
			+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN p.amount ELSE 0 END) as sale,"
			+ "SUM(CASE WHEN p.pay_type = 'REFUND' THEN p.amount ELSE 0 END) as refund "
			+ "FROM pc_invoice_payment p WHERE p.merchant_id = ?1 AND p.paid_date BETWEEN ?2 AND ?3 AND p.status in ('captured','refund') "
			+ "AND p.deleted = FALSE GROUP BY CAST(p.paid_date as DATE) ORDER BY CAST(p.paid_date as DATE);", nativeQuery = true)
	public List<Object[]> findDailyPayListForMerchant(Integer merchantId, Date startDate, Date endDate);

	@Query("SELECT p from InvoicePayment p WHERE p.paidDate BETWEEN ?1 AND ?2 AND p.deleted = FALSE")
	public List<InvoicePayment> findPaysForAdmin(Date startDate, Date endDate);

	@Query("SELECT p from InvoicePayment p WHERE p.merchant = ?1 AND p.paidDate BETWEEN ?2 AND ?3 AND p.deleted = FALSE")
	public List<InvoicePayment> findPaysForMerchant(Merchant merchant, Date startDate, Date endDate);

}
