package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.MerchantUser;

@Repository
public interface MerchantUserRepository extends JpaRepository<MerchantUser, Integer> {

	public MerchantUser findByUserId(Integer userId);

	public List<MerchantUser> findByMerchantId(Integer merchantId);

}
