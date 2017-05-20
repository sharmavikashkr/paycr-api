package com.paycr.dashboard.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.data.repository.UserRoleRepository;
import com.paycr.common.service.SecurityService;
import com.paycr.common.service.UserRoleService;
import com.paycr.common.type.Role;
import com.paycr.dashboard.service.UserService;
import com.paycr.dashboard.validation.UserValidator;

@RestController
@RequestMapping("/common")
public class CommonController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserRoleRepository userRoleRepo;

	@Autowired
	private PricingRepository priceRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private MerchantUserRepository merUserRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserRoleService urService;

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/pricings")
	public List<Pricing> getPricings() {
		List<Pricing> pricings = priceRepo.findAll();
		return pricings;
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/roles")
	public String[] getRoles() {
		PcUser user = secSer.findLoggedInUser();
		return urService.getUserRoles(user);
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/users")
	public List<PcUser> getUsers() {
		List<PcUser> myUsers = new ArrayList<PcUser>();
		if (secSer.isMerchantUser()) {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			List<MerchantUser> merUsers = merUserRepo.findByMerchantId(merchant.getId());
			for (MerchantUser merUser : merUsers) {
				PcUser user = userRepo.findOne(merUser.getUserId());
				if (Arrays.asList(urService.getUserRoles(user)).contains("ROLE_MERCHANT_USER")) {
					myUsers.add(userRepo.findOne(merUser.getUserId()));
				}
			}
		} else {
			List<UserRole> userRoles = userRoleRepo.findByRole(Role.ROLE_ADMIN_USER);
			for (UserRole userRole : userRoles) {
				myUsers.add(userRole.getPcUser());
			}
		}
		return myUsers;
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/create/user")
	public void createUser(@RequestBody PcUser user) {
		userValidator.validate(user);
		Date timeNow = new Date();
		if (secSer.isMerchantUser()) {
			user.setCreated(timeNow);
			user.setPassword(bcPassEncode.encode("password@123"));
			List<UserRole> userRoles = new ArrayList<UserRole>();
			UserRole userRole = new UserRole();
			userRole.setRole(Role.ROLE_MERCHANT_USER);
			userRole.setPcUser(user);
			user.setUserRoles(userRoles);
			userRoles.add(userRole);
			user.setActive(true);
			userRepo.save(user);

			Merchant merchant = secSer.getMerchantForLoggedInUser();
			MerchantUser merUser = new MerchantUser();
			merUser.setMerchantId(merchant.getId());
			merUser.setUserId(user.getId());
			merUserRepo.save(merUser);
			userService.sendResetLink(user);
		} else {
			user.setCreated(timeNow);
			user.setPassword(bcPassEncode.encode("password@123"));
			List<UserRole> userRoles = new ArrayList<UserRole>();
			UserRole userRole = new UserRole();
			userRole.setRole(Role.ROLE_ADMIN_USER);
			userRole.setPcUser(user);
			user.setUserRoles(userRoles);
			userRoles.add(userRole);
			user.setActive(true);
			userRepo.save(user);
		}
	}

	@PreAuthorize("hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/toggle/user/{userId}")
	public void toggleUser(@PathVariable("userId") Integer userId) {
		PcUser user = userRepo.findOne(userId);
		if (getUsers().contains(user)) {
			if (user.isActive()) {
				user.setActive(false);
			} else {
				user.setActive(true);
			}
			userRepo.save(user);
		}
	}

}
