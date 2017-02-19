package com.payme.common.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.MerchantUser;

@Repository
public interface MerchantUserRepository extends CrudRepository<MerchantUser, Integer> {

	public MerchantUser findByUserId(Integer userId);

}
