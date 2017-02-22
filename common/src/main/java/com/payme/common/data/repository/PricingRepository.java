package com.payme.common.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.Pricing;

@Repository
public interface PricingRepository extends CrudRepository<Pricing, Integer> {

}
