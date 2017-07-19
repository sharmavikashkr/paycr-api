package com.paycr.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.MerchantPricing;

@Repository
public interface MerchantPricingRepository extends JpaRepository<MerchantPricing, Integer> {

}
