package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Pricing;
import com.paycr.common.type.PricingType;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Integer> {

	public Pricing findByCodeAndActive(String code, boolean active);

	public Pricing findByCode(String code);

	public List<Pricing> findByTypeAndActive(PricingType type, boolean active);

}
