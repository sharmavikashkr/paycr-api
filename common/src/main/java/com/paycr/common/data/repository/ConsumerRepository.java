package com.paycr.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Consumer;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Integer> {

	public Consumer findByEmailAndMobile(String email, String mobile);

	public Consumer findByEmail(String email);

	public Consumer findByMobile(String mobile);
}
