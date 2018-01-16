package com.paycr.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

	public Promotion findByEmail(String email);

}
