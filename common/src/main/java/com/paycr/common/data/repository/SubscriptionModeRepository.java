package com.paycr.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.SubscriptionMode;

@Repository
public interface SubscriptionModeRepository extends JpaRepository<SubscriptionMode, Integer> {

	public SubscriptionMode findByActiveAndName(boolean active, String name);

}
