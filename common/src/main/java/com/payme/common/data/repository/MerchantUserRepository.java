package com.payme.common.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.MerchantUser;

@Repository
public interface MerchantUserRepository extends MongoRepository<MerchantUser, String> {

	public MerchantUser findByUserId(String userId);

}
