package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.type.PayType;

@Repository
public interface ExpensePaymentRepository extends JpaRepository<ExpensePayment, Integer> {

	public List<ExpensePayment> findByExpenseCode(String expenseCode);

	public List<ExpensePayment> findByExpenseCodeAndPayType(String expenseCode, PayType payType);

	@Query(value = "SELECT COUNT(p) as count, SUM(p.amount) as sum FROM pc_expense_payment p WHERE p.pay_type = ?1 AND "
			+ "p.status in ('captured','refund') AND p.created BETWEEN ?2 AND ?3", nativeQuery = true)
	public List<Object[]> findCountAndSum(String payType, Date startDate, Date endDate);

	@Query(value = "SELECT COUNT(p) as count, SUM(p.amount) as sum "
			+ "FROM pc_expense_payment p WHERE p.merchant_id = ?1 AND p.pay_type = ?2 AND "
			+ "p.status in ('captured','refund') AND p.created BETWEEN ?3 AND ?4", nativeQuery = true)
	public List<Object[]> findCountAndSumForMerchant(Integer merchantId, String payType, Date startDate, Date endDate);

	@Query(value = "SELECT CAST(CAST(p.created as DATE) as VARCHAR(10)) as date,"
			+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN p.amount ELSE 0 END) as sale,"
			+ "SUM(CASE WHEN p.pay_type = 'REFUND' THEN p.amount ELSE 0 END) as refund "
			+ "FROM pc_expense_payment p WHERE p.created BETWEEN ?1 AND ?2 AND p.status in ('captured','refund') "
			+ "GROUP BY CAST(p.created as DATE) ORDER BY CAST(p.created as DATE);", nativeQuery = true)
	public List<Object[]> findDailyPayList(Date startDate, Date endDate);

	@Query(value = "SELECT CAST(CAST(p.created as DATE) as VARCHAR(10)) as date,"
			+ "SUM(CASE WHEN p.pay_type = 'SALE' THEN p.amount ELSE 0 END) as sale,"
			+ "SUM(CASE WHEN p.pay_type = 'REFUND' THEN p.amount ELSE 0 END) as refund "
			+ "FROM pc_expense_payment p WHERE p.merchant_id = ?3 AND p.created BETWEEN ?1 AND ?2 AND p.status in ('captured','refund') "
			+ "GROUP BY CAST(p.created as DATE) ORDER BY CAST(p.created as DATE);", nativeQuery = true)
	public List<Object[]> findDailyPayListForMerchant(Date startDate, Date endDate, Integer merchantId);

	@Query("SELECT p from ExpensePayment p WHERE p.created BETWEEN ?1 AND ?2")
	public List<ExpensePayment> findPaysForAdmin(Date startDate, Date endDate);

	@Query("SELECT p from ExpensePayment p WHERE p.merchant = ?1 AND p.created BETWEEN ?2 AND ?3")
	public List<ExpensePayment> findPaysForMerchant(Merchant merchant, Date startDate, Date endDate);

}
