package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.ConsumerCategory;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface ConsumerCategoryRepository extends JpaRepository<ConsumerCategory, Integer> {

	public List<ConsumerCategory> findByConsumer(Consumer consumer);

	@Query("SELECT DISTINCT cc.name FROM ConsumerCategory cc WHERE cc.consumer.merchant = ?1")
	public List<String> findCategoriesForMerchant(Merchant merchant);

	public ConsumerCategory findByConsumerAndName(Consumer consumer, String name);

	@Query("SELECT cc.consumer FROM ConsumerCategory cc WHERE cc.name = ?1 AND cc.value = ?2")
	public List<Consumer> findByNameAndValue(String name, String value);

	@Query("SELECT DISTINCT cc.value FROM ConsumerCategory cc WHERE cc.consumer.merchant = ?1 AND cc.name = ?2")
	public List<String> findValuesForCategory(Merchant merchant, String category);

	public ConsumerCategory findByConsumerAndId(Consumer consumer, Integer id);

}
