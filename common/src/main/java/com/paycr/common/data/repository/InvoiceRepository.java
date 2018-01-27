package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.type.InvoiceStatus;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

	public Invoice findByInvoiceCode(String invoiceCode);

	public Invoice findByInvoiceCodeAndMerchant(String invoiceCode, Merchant merchant);

	@Query("SELECT i from Invoice i WHERE i.consumer.email = ?1 OR i.consumer.mobile = ?2 ORDER BY i.id DESC")
	public List<Invoice> findInvoicesForConsumer(String email, String mobile);

	@Query("SELECT i from Invoice i WHERE i.status != 'PAID' AND i.status != 'EXPIRED' AND i.expiry < ?1")
	public List<Invoice> findInvoicesToExpire(Date date);

	@Query(value = "SELECT COUNT(i) as count, SUM(i.pay_amount) as sum FROM pc_invoice i WHERE i.status = ?1 AND "
			+ "i.invoice_date BETWEEN ?2 AND ?3", nativeQuery = true)
	public List<Object[]> findCountAndSum(String status, Date startDate, Date endDate);

	@Query(value = "SELECT COUNT(i) as count, SUM(i.pay_amount) as sum FROM pc_invoice i WHERE i.merchant_id = ?1 AND "
			+ "i.status = ?2 AND i.invoice_date BETWEEN ?3 AND ?4", nativeQuery = true)
	public List<Object[]> findCountAndSumForMerchant(Integer merchantId, String status, Date startDate, Date endDate);

	@Query(value = "SELECT i FROM Invoice i WHERE i.merchant = ?1 AND i.status in ?2 AND i.invoiceDate BETWEEN ?3 AND ?4")
	public List<Invoice> findInvoicesForMerchant(Merchant merchant, List<InvoiceStatus> statuses, Date startDate,
			Date endDate);

	@Query("SELECT i from Invoice i WHERE i.note.noteCode = ?1")
	public Invoice findByNoteCode(String noteCode);

}
