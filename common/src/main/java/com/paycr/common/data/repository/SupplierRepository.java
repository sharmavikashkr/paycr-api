package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

	public List<Supplier> findByMerchant(Merchant merchant);

	public Supplier findByMerchantAndId(Merchant merchant, Integer id);

	@Query("SELECT c FROM Supplier c WHERE c.merchant = ?1 AND c.email = ?2 AND c.mobile = ?3")
	public Supplier findSupplierForMerchant(Merchant merchant, String email, String mobile);

}
