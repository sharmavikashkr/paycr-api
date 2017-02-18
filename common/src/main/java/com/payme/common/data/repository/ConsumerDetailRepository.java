package com.payme.common.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.ConsumerDetail;

@Repository
public interface ConsumerDetailRepository extends MongoRepository<ConsumerDetail, String> {

	public ConsumerDetail findByInvoiceId(String invoiceId);

	public ConsumerDetail findByMerchantId(String merchantId);
}
