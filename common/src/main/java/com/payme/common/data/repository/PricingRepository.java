package com.payme.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.Pricing;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Integer> {

}
