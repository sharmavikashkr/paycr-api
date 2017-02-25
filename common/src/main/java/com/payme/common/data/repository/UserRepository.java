package com.payme.common.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.PmUser;

@Repository
public interface UserRepository extends JpaRepository<PmUser, Integer> {

	public PmUser findByEmail(String email);

}
