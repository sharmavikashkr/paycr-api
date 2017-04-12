package com.paycr.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.SubscriptionSetting;

@Repository
public interface SubscriptionSettingRepository extends JpaRepository<SubscriptionSetting, Integer> {

	public SubscriptionSetting findByActive(boolean active);

}
