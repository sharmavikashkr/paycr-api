package com.payme.common.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

	public User findByEmail(String email);
}
