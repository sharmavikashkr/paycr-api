package com.paycr.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Integer> {

	public Merchant findByAccessKey(String accessKey);

	public Merchant findByEmail(String email);

}
