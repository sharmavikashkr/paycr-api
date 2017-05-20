package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.UserRole;
import com.paycr.common.type.Role;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

	public List<UserRole> findByRole(Role role);

}
