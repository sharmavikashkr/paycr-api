package com.payme.dashboard.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping("user")
public class UserController {

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

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void createUser() {
		PmUser user = new PmUser();
		user.setCreated(new Date());
		user.setName("Test Merchant Admin");
		user.setEmail("merchant@payme.com");
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile("9970197591");
		List<UserRole> userRoles = new ArrayList<UserRole>();
		UserRole userRole = new UserRole();
		userRole.setRole(Role.ROLE_MERCHANT);
		userRole.setPmUser(user);
		userRoles.add(userRole);
		user.setUserRoles(userRoles);
		userRepo.save(user);

		String secretKey = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(new Date().getTime()));
		String accessKey = secretKey + secretKey.toLowerCase() + secretKey.toUpperCase();
		accessKey = RandomIdGenerator.generateAccessKey(accessKey.toCharArray());
		Merchant merchant = new Merchant();
		merchant.setCreated(new Date());
		merchant.setAccessKey(accessKey);
		merchant.setSecretKey(secretKey);
		merchant.setName("Test Merchant");
		merchant.setEmail("merchant@payme.com");
		merchant.setMobile("9970197591");
		merRepo.save(merchant);

		MerchantUser merUser = new MerchantUser();
		merUser.setMerchantId(merchant.getId());
		merUser.setUserId(user.getId());
		merUserRepo.save(merUser);

		user = new PmUser();
		user.setCreated(new Date());
		user.setName("Test Admin");
		user.setEmail("admin@payme.com");
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile("9970197591");
		userRoles.clear();
		userRole = new UserRole();
		userRole.setRole(Role.ROLE_ADMIN);
		userRole.setPmUser(user);
		userRoles.add(userRole);
		user.setUserRoles(userRoles);
		userRepo.save(user);
	}

}
