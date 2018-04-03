package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.type.InvoiceStatus;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

	public List<Inventory> findByMerchant(Merchant merchant);

	public Inventory findByMerchantAndCode(Merchant merchant, String code);

	@Query(value = "SELECT SUM(i.quantity) as count, SUM(i.inventory.rate * i.quantity) as sum "
			+ "FROM InvoiceItem i WHERE i.invoice.merchant = ?1 AND i.inventory.id = ?2 AND "
			+ "i.invoice.status = ?3 AND i.invoice.deleted = FALSE", nativeQuery = false)
	public List<Object[]> findCountAndSumForMerchant(Merchant merchant, Integer inventoryId, InvoiceStatus status);
}
