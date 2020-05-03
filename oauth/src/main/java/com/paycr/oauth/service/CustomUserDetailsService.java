package com.paycr.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paycr.oauth.data.domain.CustomUserDetails;
import com.paycr.oauth.data.domain.PcUser;
import com.paycr.oauth.data.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		PcUser user = userRepo.findByEmail(email);
		if (user != null) {
			if (!user.isActive()) {
				return null;
			}
			String password = user.getPassword();
			String[] roles = user.getUserRoles().stream().map(r -> r.getRole()).toArray(size -> new String[size]);
			CustomUserDetails customUserDetails = new CustomUserDetails(email, password, roles);
			return customUserDetails;
		}
		return null;
	}
}
