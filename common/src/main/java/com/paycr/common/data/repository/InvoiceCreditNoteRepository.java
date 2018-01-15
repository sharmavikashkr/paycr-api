package com.paycr.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.InvoiceCreditNote;

@Repository
public interface InvoiceCreditNoteRepository extends JpaRepository<InvoiceCreditNote, Integer> {

	public InvoiceCreditNote findByInvoiceCode(String invoiceCode);

	public InvoiceCreditNote findByNoteCode(String noteCode);

}
