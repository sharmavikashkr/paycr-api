package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

	public List<Notification> findByUserIdAndMerchantIdOrderByIdDesc(Integer userId, Integer merchantId,
			Pageable pageable);

}
