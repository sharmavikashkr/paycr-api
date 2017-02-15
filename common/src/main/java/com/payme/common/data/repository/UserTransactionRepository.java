package com.payme.common.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.User;

@Repository
public interface UserTransactionRepository extends MongoRepository<User, String> {

	public User findByEmailAndPassword(String email, String password);

}
