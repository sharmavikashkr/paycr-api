package com.payme.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

	public Invoice findByInvoiceCode(String invoiceCode);

	public List<Invoice> findByMerchant(Integer merchant);
}
