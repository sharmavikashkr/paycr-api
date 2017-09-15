package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.BulkUpload;

@Repository
public interface BulkUploadRepository extends JpaRepository<BulkUpload, Integer> {

	public List<BulkUpload> findByInvoiceCode(String invoiceCode);

}
