package com.paycr.common.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.Role;

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

	@Autowired
	private TokenStore tokenStore;

	public PcUser findLoggedInUser() {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetails instanceof UserDetails) {
			return userRepo.findByEmail(((UserDetails) userDetails).getUsername());
		}
		return null;
	}

	public PcUser findLoggedInUser(String token) {
		try {
			OAuth2Authentication oauth = tokenStore.readAuthentication(tokenStore.readAccessToken(token));
			UserDetails userDetails = userDetailsService.loadUserByUsername(oauth.getUserAuthentication().getName());
			return userRepo.findByEmail(((UserDetails) userDetails).getUsername());
		} catch (Exception ex) {
			return null;
		}
	}

	public boolean isMerchantUser() {
		PcUser user = findLoggedInUser();
		String[] roles = userRoleService.getUserRoles(user);
		if (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_USER.name())) {
			return true;
		}
		return false;
	}

	public Merchant getMerchantForLoggedInUser() {
		PcUser user = findLoggedInUser();
		String[] roles = userRoleService.getUserRoles(user);
		if (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_USER.name())) {
			MerchantUser merUser = merUserRepo.findByUserId(user.getId());
			return merRepo.findOne(merUser.getMerchantId());
		}
		return null;
	}

	public Merchant getMerchantForLoggedInUser(String token) {
		PcUser user = findLoggedInUser(token);
		if (user == null) {
			return null;
		}
		String[] roles = userRoleService.getUserRoles(user);
		if (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_USER.name())) {
			MerchantUser merUser = merUserRepo.findByUserId(user.getId());
			return merRepo.findOne(merUser.getMerchantId());
		}
		return null;
	}

	public boolean isLoggedInUserAdmin(String token) {
		PcUser user = findLoggedInUser(token);
		if (user == null) {
			return false;
		}
		String[] roles = userRoleService.getUserRoles(user);
		if (Arrays.asList(roles).contains(Role.ROLE_ADMIN.name())
				|| Arrays.asList(roles).contains(Role.ROLE_ADMIN_USER.name())) {
			return true;
		}
		return false;
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
