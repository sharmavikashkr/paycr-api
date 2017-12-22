package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.BulkInventoryUpload;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface BulkInventoryUploadRepository extends JpaRepository<BulkInventoryUpload, Integer> {

	public List<BulkInventoryUpload> findByMerchant(Merchant merchant);

}
