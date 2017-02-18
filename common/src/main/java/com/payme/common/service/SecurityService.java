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
import com.payme.common.data.domain.User;
import com.payme.common.data.repository.MerchantRepository;
import com.payme.common.data.repository.MerchantUserRepository;
import com.payme.common.data.repository.UserRepository;

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

	public User findLoggedInUser() {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetails instanceof UserDetails) {
			return userRepo.findByEmail(((UserDetails) userDetails).getUsername());
		}
		return null;
	}

	public boolean isMerchantUser() {
		User user = findLoggedInUser();
		if (Arrays.asList(user.getRoles()).contains("ROLE_MERCHANT")) {
			return true;
		}
		return false;
	}

	public Merchant getMerchantForLoggedInUser() {
		User user = findLoggedInUser();
		if (Arrays.asList(user.getRoles()).contains("ROLE_MERCHANT")) {
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
