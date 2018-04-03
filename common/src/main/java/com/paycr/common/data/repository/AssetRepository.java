package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.type.ExpenseStatus;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer> {

	public List<Asset> findByMerchant(Merchant merchant);

	public Asset findByMerchantAndCode(Merchant merchant, String code);

	@Query(value = "SELECT SUM(i.quantity) as count, SUM(i.asset.rate * i.quantity) as sum "
			+ "FROM ExpenseItem i WHERE i.expense.merchant = ?1 AND i.asset.id = ?2 AND "
			+ "i.expense.status = ?3 AND i.expense.deleted = FALSE", nativeQuery = false)
	public List<Object[]> findCountAndSumForMerchant(Merchant merchant, Integer assetId, ExpenseStatus status);
}
