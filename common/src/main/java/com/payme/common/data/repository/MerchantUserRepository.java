package com.payme.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.MerchantUser;

@Repository
public interface MerchantUserRepository extends JpaRepository<MerchantUser, Integer> {

	public MerchantUser findByUserId(Integer userId);

}
