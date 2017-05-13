package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

	public Invoice findByInvoiceCode(String invoiceCode);

	public Invoice findByInvoiceCodeAndMerchant(String invoiceCode, Integer merchant);

	@Query("SELECT i from Invoice i WHERE i.consumer.email = ?1 OR i.consumer.mobile = ?2 ORDER BY i.id DESC")
	public List<Invoice> findInvoicesForMerchant(String email, String mobile);
}
