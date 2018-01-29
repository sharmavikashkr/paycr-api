package com.paycr.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.CustomUserDetails;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.service.UserRoleService;
import com.paycr.common.util.CommonUtil;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserRoleService userRoleService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		PcUser user = userRepo.findByEmail(email);
		if (CommonUtil.isNotNull(user)) {
			if (!user.isActive()) {
				return null;
			}
			String password = user.getPassword();
			String[] roles = userRoleService.getUserRoles(user);
			CustomUserDetails customUserDetails = new CustomUserDetails(email, password, roles);
			return customUserDetails;
		}
		return null;
	}
}
