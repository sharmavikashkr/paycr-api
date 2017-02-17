package com.payme.common.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.payme.common.data.domain.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
}
