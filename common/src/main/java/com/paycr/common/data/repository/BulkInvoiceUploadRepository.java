package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.BulkInvoiceUpload;

@Repository
public interface BulkInvoiceUploadRepository extends JpaRepository<BulkInvoiceUpload, Integer> {

	public List<BulkInvoiceUpload> findByInvoiceCode(String invoiceCode);

}
