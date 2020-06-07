package com.paycr.common.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.Role;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.RoleUtil;

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
		List<String> roleList = Arrays.asList(roles);
		return RoleUtil.MERCHANT_ROLES.stream().anyMatch(r -> roleList.contains(r));
	}

	public Merchant getMerchantForLoggedInUser() {
		if (isMerchantUser()) {
			PcUser user = findLoggedInUser();
			MerchantUser merUser = merUserRepo.findByUserId(user.getId());
			if (CommonUtil.isNull(merUser)) {
				return null;
			}
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
		List<String> roleList = Arrays.asList(roles);
		return (RoleUtil.PAYCR_ROLES.stream().anyMatch(r -> roleList.contains(r)));
	}
}
