package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerFlag;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface ConsumerFlagRepository extends JpaRepository<ConsumerFlag, Integer> {

	@Query("SELECT DISTINCT cf.name FROM ConsumerFlag cf WHERE cf.consumer.merchant = ?1")
	public List<String> findFlagsForMerchant(Merchant merchant);

	public ConsumerFlag findByConsumerAndName(Consumer consumer, String name);

	@Query("SELECT cf.consumer FROM ConsumerFlag cf WHERE cf.name = ?1")
	public List<Consumer> findByName(String name);

	public ConsumerFlag findByConsumerAndId(Consumer consumer, Integer id);

	@Transactional
	@Modifying
	@Query("DELETE FROM ConsumerFlag cf WHERE cf.consumer = ?1")
	public void deleteForConsumer(Consumer consumer);

}
