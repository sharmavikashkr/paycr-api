package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.BulkCategory;

@Repository
public interface BulkCategoryRepository extends JpaRepository<BulkCategory, Integer> {

	public List<BulkCategory> findByInvoiceCode(String invoiceCode);

}
