package com.paycr.common.data.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

	public List<Inventory> findByMerchant(Merchant merchant);

	public Inventory findByMerchantAndNameAndRate(Merchant merchant, String name, BigDecimal rate);
}
