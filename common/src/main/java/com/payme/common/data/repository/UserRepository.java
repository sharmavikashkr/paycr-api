package com.payme.common.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.PmUser;

@Repository
public interface UserRepository extends CrudRepository<PmUser, Integer> {

	public PmUser findByEmail(String email);

}
