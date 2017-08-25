package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Integer> {

	public List<Consumer> findByMerchant(Merchant merchant);

	public Consumer findByMerchantAndEmailAndMobile(Merchant merchant, String email, String mobile);
}
