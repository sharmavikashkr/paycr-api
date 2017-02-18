package com.payme.common.data.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.ItemDetail;

@Repository
public interface ItemDetailRepository extends MongoRepository<ItemDetail, String> {

	public List<ItemDetail> findByInvoiceId(String invoiceId);

	public List<ItemDetail> findByMerchantId(String merchantId);
}
