package com.payme.common.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.Merchant;

@Repository
public interface MerchantRepository extends CrudRepository<Merchant, Integer> {

	public Merchant findByAccessKey(String accessKey);

}
