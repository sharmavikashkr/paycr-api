package com.payme.common.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.Merchant;

@Repository
public interface MerchantRepository extends MongoRepository<Merchant, String> {

	public Merchant findByAccessKey(String accessKey);

}
