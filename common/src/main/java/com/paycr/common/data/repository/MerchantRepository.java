package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Integer> {

	public Merchant findByAccessKey(String accessKey);

	public Merchant findByEmail(String email);

	@Query("select i.consumer from Invoice i where i.merchant = ?0")
	public List<Consumer> findMyConsumers(Integer merchant);

}
