package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Integer> {

	public List<Consumer> findByMerchant(Merchant merchant);

	@Query("SELECT c FROM Consumer c WHERE c.merchant = ?1 AND c.email = ?2 AND c.mobile = ?3")
	public Consumer findConsumerForMerchant(Merchant merchant, String email, String mobile);
	
	
}
