package com.payme.dashboard.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payme.common.data.domain.Merchant;
import com.payme.common.data.domain.MerchantUser;
import com.payme.common.data.domain.PmUser;
import com.payme.common.data.domain.UserRole;
import com.payme.common.data.repository.MerchantRepository;
import com.payme.common.data.repository.MerchantUserRepository;
import com.payme.common.data.repository.UserRepository;
import com.payme.common.type.Role;
import com.payme.common.util.HmacSignerUtil;
import com.payme.common.util.RandomIdGenerator;

@RestController
@RequestMapping("merchant")
public class MerchantController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private MerchantUserRepository merUserRepo;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("new")
	public void newMerchant(@RequestBody Merchant merchant) {
		String secretKey = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(new Date().getTime()));
		String accessKey = secretKey + secretKey.toLowerCase() + secretKey.toUpperCase();
		accessKey = RandomIdGenerator.generateAccessKey(accessKey.toCharArray());
		merchant.setAccessKey(accessKey);
		merchant.setSecretKey(secretKey);
		merchant.setActive(true);
		merRepo.save(merchant);

		PmUser user = new PmUser();
		user.setName(merchant.getAdminName());
		user.setEmail(merchant.getEmail());
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile(merchant.getMobile());
		List<UserRole> userRoles = new ArrayList<UserRole>();
		UserRole userRole = new UserRole();
		userRole.setRole(Role.ROLE_MERCHANT);
		userRole.setPmUser(user);
		user.setUserRoles(userRoles);
		userRoles.add(userRole);
		user.setActive(true);
		userRepo.save(user);

		MerchantUser merUser = new MerchantUser();
		merUser.setMerchantId(merchant.getId());
		merUser.setUserId(user.getId());
		merUserRepo.save(merUser);
	}

}
