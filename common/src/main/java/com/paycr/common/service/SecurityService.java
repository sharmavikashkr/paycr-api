package com.paycr.common.service;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.Role;
import com.paycr.common.util.CommonUtil;

@Service
public class SecurityService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MerchantUserRepository merUserRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private UserRoleService userRoleService;

	// @Autowired
	// private TokenStore tokenStore;

	public PcUser findLoggedInUser() {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetails instanceof UserDetails) {
			return userRepo.findByEmail(((UserDetails) userDetails).getUsername());
		}
		return null;
	}

	public PcUser findLoggedInUser(String token) {
		try {
			// OAuth2Authentication oauth =
			// tokenStore.readAuthentication(tokenStore.readAccessToken(token));
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			// UserDetails userDetails = (UserDetails)
			// oauth.getUserAuthentication().getPrincipal();
			return userRepo.findByEmail(userDetails.getUsername());
		} catch (Exception ex) {
			return null;
		}
	}

	public boolean isMerchantUser() {
		PcUser user = findLoggedInUser();
		String[] roles = userRoleService.getUserRoles(user);
		return (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_FINANCE.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_OPS.name()));
	}

	public Merchant getMerchantForLoggedInUser() {
		PcUser user = findLoggedInUser();
		String[] roles = userRoleService.getUserRoles(user);
		if (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_FINANCE.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_OPS.name())) {
			MerchantUser merUser = merUserRepo.findByUserId(user.getId());
			Optional<Merchant> merchantOpt = merRepo.findById(merUser.getMerchantId());
			return merchantOpt.isPresent() ? merchantOpt.get() : null;
		}
		return null;
	}

	public Merchant getMerchantForLoggedInUser(String token) {
		PcUser user = findLoggedInUser(token);
		if (CommonUtil.isNull(user)) {
			return null;
		}
		String[] roles = userRoleService.getUserRoles(user);
		if (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_FINANCE.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_OPS.name())) {
			MerchantUser merUser = merUserRepo.findByUserId(user.getId());
			Optional<Merchant> merchantOpt = merRepo.findById(merUser.getMerchantId());
			return merchantOpt.isPresent() ? merchantOpt.get() : null;
		}
		return null;
	}

	public boolean isPaycrUser(String token) {
		PcUser user = findLoggedInUser(token);
		if (CommonUtil.isNull(user)) {
			return false;
		}
		String[] roles = userRoleService.getUserRoles(user);
		return (Arrays.asList(roles).contains(Role.ROLE_PAYCR.name())
				|| Arrays.asList(roles).contains(Role.ROLE_PAYCR_SUPERVISOR.name())
				|| Arrays.asList(roles).contains(Role.ROLE_PAYCR_FINANCE.name())
				|| Arrays.asList(roles).contains(Role.ROLE_PAYCR_OPS.name())
				|| Arrays.asList(roles).contains(Role.ROLE_PAYCR_ADVISOR.name()));
	}
}
