package com.payme.common.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.Invoice;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {

	public Invoice findByInvoiceCode(String mtx);
}
