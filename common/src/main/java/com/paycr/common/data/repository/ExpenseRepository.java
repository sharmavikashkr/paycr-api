package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.type.ExpenseStatus;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

	public Expense findByExpenseCode(String expenseCode);

	public Expense findByExpenseCodeAndMerchant(String expenseCode, Merchant merchant);

	@Query("SELECT e from Expense e WHERE e.supplier.email = ?1 OR e.supplier.mobile = ?2 ORDER BY e.id DESC")
	public List<Expense> findExpensesForSupplier(String email, String mobile);

	@Query(value = "SELECT COUNT(e) as count, SUM(e.pay_amount) as sum FROM pc_expense e WHERE e.status = ?1 AND "
			+ "e.invoice_date BETWEEN ?2 AND ?3", nativeQuery = true)
	public List<Object[]> findCountAndSum(String status, Date startDate, Date endDate);

	@Query(value = "SELECT COUNT(e) as count, SUM(e.pay_amount) as sum FROM pc_expense e WHERE e.merchant_id = ?1 AND "
			+ "e.status = ?2 AND e.invoice_date BETWEEN ?3 AND ?4", nativeQuery = true)
	public List<Object[]> findCountAndSumForMerchant(Integer merchantId, String status, Date startDate, Date endDate);

	@Query(value = "SELECT e FROM Expense e WHERE e.merchant = ?1 AND e.status in ?2 AND e.invoiceDate BETWEEN ?3 AND ?4")
	public List<Expense> findExpensesForMerchant(Merchant merchant, List<ExpenseStatus> statuses, Date startDate,
			Date endDate);

	@Query("SELECT e from Expense e WHERE e.note.noteCode = ?1")
	public Expense findByNoteCode(String noteCode);

}
