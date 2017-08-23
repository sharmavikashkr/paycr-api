package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Item;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

	public Invoice findByInvoiceCode(String invoiceCode);

	public Invoice findByInvoiceCodeAndMerchant(String invoiceCode, Merchant merchant);

	@Query("SELECT i from Invoice i WHERE i.consumer.email = ?1 OR i.consumer.mobile = ?2 ORDER BY i.id DESC")
	public List<Invoice> findInvoicesForConsumer(String email, String mobile);

	@Query("SELECT DISTINCT i.consumer from Invoice i WHERE i.merchant = ?1")
	public List<Consumer> findConsumersForMerchant(Merchant merchant);

	@Query("SELECT DISTINCT it from Item it WHERE it.invoice.merchant = ?1")
	public List<Item> findItemsForMerchant(Merchant merchant);
}
