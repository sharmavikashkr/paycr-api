package com.paycr.common.service;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

	public PcUser findLoggedInUser() {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return (username != null) ? userRepo.findByEmail(username) : null;
	}

	public boolean isMerchantUser() {
		PcUser user = findLoggedInUser();
		String[] roles = user.getUserRoles().stream().map(r -> r.getRole().name()).toArray(size -> new String[size]);
		return (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_FINANCE.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_OPS.name()));
	}

	public Merchant getMerchantForLoggedInUser() {
		PcUser user = findLoggedInUser();
		String[] roles = user.getUserRoles().stream().map(r -> r.getRole().name()).toArray(size -> new String[size]);
		if (Arrays.asList(roles).contains(Role.ROLE_MERCHANT.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_FINANCE.name())
				|| Arrays.asList(roles).contains(Role.ROLE_MERCHANT_OPS.name())) {
			MerchantUser merUser = merUserRepo.findByUserId(user.getId());
			Optional<Merchant> merchantOpt = merRepo.findById(merUser.getMerchantId());
			return merchantOpt.isPresent() ? merchantOpt.get() : null;
		}
		return null;
	}

	public boolean isPaycrUser() {
		PcUser user = findLoggedInUser();
		if (CommonUtil.isNull(user)) {
			return false;
		}
		String[] roles = user.getUserRoles().stream().map(r -> r.getRole().name()).toArray(size -> new String[size]);
		return (Arrays.asList(roles).contains(Role.ROLE_PAYCR.name())
				|| Arrays.asList(roles).contains(Role.ROLE_PAYCR_SUPERVISOR.name())
				|| Arrays.asList(roles).contains(Role.ROLE_PAYCR_FINANCE.name())
				|| Arrays.asList(roles).contains(Role.ROLE_PAYCR_OPS.name())
				|| Arrays.asList(roles).contains(Role.ROLE_PAYCR_ADVISOR.name()));
	}
}
