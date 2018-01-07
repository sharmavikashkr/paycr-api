package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.PricingMerchant;

@Repository
public interface PricingMerchantRepository extends JpaRepository<PricingMerchant, Integer> {

	@Query("SELECT pm.pricing from PricingMerchant pm where pm.merchant = ?1")
	public List<Pricing> findPricingForMerchant(Merchant merchant);

	public PricingMerchant findByMerchantAndPricing(Merchant merchant, Pricing pricing);

	@Query("SELECT pm.merchant from PricingMerchant pm where pm.pricing = ?1")
	public List<Merchant> findMerchantsForPricing(Pricing pricing);

}
