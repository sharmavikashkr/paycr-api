package com.payme.common.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.payme.common.data.domain.Merchant;
import com.payme.common.data.domain.MerchantUser;
import com.payme.common.data.domain.PmUser;
import com.payme.common.data.repository.MerchantRepository;
import com.payme.common.data.repository.MerchantUserRepository;
import com.payme.common.data.repository.UserRepository;
import com.payme.common.type.Role;

@Service
public class SecurityService {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MerchantUserRepository merUserRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserRoleService userRoleService;

	public PmUser findLoggedInUser() {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetails instanceof UserDetails) {
			return userRepo.findByEmail(((UserDetails) userDetails).getUsername());
		}
		return null;
	}

	public boolean isMerchantUser() {
		PmUser user = findLoggedInUser();
		String[] roles = userRoleService.getUserRoles(user);
		if (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())) {
			return true;
		}
		return false;
	}

	public Merchant getMerchantForLoggedInUser() {
		PmUser user = findLoggedInUser();
		String[] roles = userRoleService.getUserRoles(user);
		if (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())) {
			MerchantUser merUser = merUserRepo.findByUserId(user.getId());
			return merRepo.findOne(merUser.getMerchantId());
		}
		return null;
	}

	public void autologin(String email, String password) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				userDetails, password, userDetails.getAuthorities());
		authenticationManager.authenticate(usernamePasswordAuthenticationToken);
		if (usernamePasswordAuthenticationToken.isAuthenticated()) {
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		}
	}
}
