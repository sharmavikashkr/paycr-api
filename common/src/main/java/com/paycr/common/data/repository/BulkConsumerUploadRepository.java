package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.BulkConsumerUpload;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface BulkConsumerUploadRepository extends JpaRepository<BulkConsumerUpload, Integer> {

	public List<BulkConsumerUpload> findByMerchant(Merchant merchant);

}
