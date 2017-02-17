package com.payme.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.payme.common.data.domain.MongoUserDetails;
import com.payme.common.data.domain.User;
import com.payme.common.data.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(email);
		if (user != null) {
			String password = user.getPassword();
			String[] roles = user.getRoles();
			MongoUserDetails mongoUserDetails = new MongoUserDetails(email, password, roles);
			return mongoUserDetails;
		}
		return null;
	}
}
