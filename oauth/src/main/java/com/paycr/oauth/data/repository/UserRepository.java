package com.paycr.oauth.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.oauth.data.domain.PcUser;

@Repository
public interface UserRepository extends JpaRepository<PcUser, Integer> {

	public PcUser findByEmail(String email);

}
