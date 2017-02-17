package com.payme.common.data.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.UserRole;

@Repository
public interface UserRoleRepository extends MongoRepository<UserRole, String> {

	public List<UserRole> findByUserId(String id);
}
