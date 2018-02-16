package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.BulkFlag;

@Repository
public interface BulkFlagRepository extends JpaRepository<BulkFlag, Integer> {

	public List<BulkFlag> findByInvoiceCode(String invoiceCode);

}
