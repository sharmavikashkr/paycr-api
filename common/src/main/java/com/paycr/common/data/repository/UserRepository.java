package com.paycr.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.PcUser;

@Repository
public interface UserRepository extends JpaRepository<PcUser, Integer> {

	public PcUser findByEmail(String email);

	public PcUser findByEmailOrMobile(String email, String mobile);

}
