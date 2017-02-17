package com.payme.dashboard.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.payme.common.data.domain.MongoUserDetails;
import com.payme.common.data.domain.Role;
import com.payme.common.data.domain.User;
import com.payme.common.data.domain.UserRole;
import com.payme.common.data.repository.RoleRepository;
import com.payme.common.data.repository.UserRepository;
import com.payme.common.data.repository.UserRoleRepository;

public class CustomerUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private UserRoleRepository userRoleRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(email);
		if (user != null) {
			String password = user.getPassword();
			List<UserRole> userRoles = userRoleRepo.findByUserId(user.getId());
			List<String> authorities = new ArrayList<String>();
			for (UserRole userRole : userRoles) {
				Role role = roleRepo.findOne(userRole.getRoleId());
				authorities.add(role.getRole());
			}
			MongoUserDetails mongoUserDetails = new MongoUserDetails(email, password,
					authorities.toArray(new String[authorities.size()]));
			return mongoUserDetails;
		}
		return null;
	}
}
