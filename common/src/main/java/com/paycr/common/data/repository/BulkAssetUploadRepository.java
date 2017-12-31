package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.BulkAssetUpload;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface BulkAssetUploadRepository extends JpaRepository<BulkAssetUpload, Integer> {

	public List<BulkAssetUpload> findByMerchant(Merchant merchant);

}
