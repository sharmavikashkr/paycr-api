package com.payme.common.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.Invoice;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

	public Invoice findByInvoiceCode(String invoiceCode);

	public List<Invoice> findByMerchant(Integer merchant);
}
