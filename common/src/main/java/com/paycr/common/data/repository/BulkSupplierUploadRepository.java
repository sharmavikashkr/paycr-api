package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.BulkSupplierUpload;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface BulkSupplierUploadRepository extends JpaRepository<BulkSupplierUpload, Integer> {

	public List<BulkSupplierUpload> findByMerchant(Merchant merchant);

}
