package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.paycr.common.bean.Access;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.data.repository.UserRoleRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.Role;
import com.paycr.common.type.UserType;
import com.paycr.common.util.CommonUtil;
import com.paycr.dashboard.validation.UserValidator;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserRoleRepository userRoleRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private MerchantUserRepository merUserRepo;

	@Autowired
	private AccessService accessService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private InvoiceRepository invRepo;

	public PcUser saveUser(PcUser user) {
		return userRepo.save(user);
	}

	public PcUser saveAddress(PcUser user, Address addr) {
		if (CommonUtil.isNull(addr) || CommonUtil.isEmpty(addr.getAddressLine1()) || CommonUtil.isEmpty(addr.getCity())
				|| CommonUtil.isEmpty(addr.getState()) || CommonUtil.isEmpty(addr.getPincode())
				|| CommonUtil.isEmpty(addr.getCountry())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Address");
		}
		Address address = user.getAddress();
		if (CommonUtil.isNull(address)) {
			address = new Address();
		}
		address.setAddressLine1(addr.getAddressLine1());
		address.setAddressLine2(addr.getAddressLine2());
		address.setCity(addr.getCity());
		address.setState(addr.getState());
		address.setCountry(addr.getCountry());
		address.setPincode(addr.getPincode());
		user.setAddress(address);
		return userRepo.save(user);
	}

	public PcUser getUserByEmail(String userEmail) {
		return userRepo.findByEmail(userEmail);
	}

	public List<Invoice> getMyInvoices(PcUser user) {
		List<Invoice> myInvoices = invRepo.findInvoicesForConsumer(user.getEmail(), user.getMobile());
		return myInvoices;
	}

	public Access loadAccess(PcUser user) {
		Access access = new Access();
		if (UserType.ADMIN.equals(user.getUserType())) {
			access.setAdmin(true);
			access.setSupervisor(true);
			access.setFinance(true);
			access.setOps(true);
			access.setAdvisor(true);
		} else if (UserType.SUPERVISOR.equals(user.getUserType())) {
			access.setAdmin(false);
			access.setSupervisor(true);
			access.setFinance(true);
			access.setOps(true);
			access.setAdvisor(true);
		} else if (UserType.FINANCE.equals(user.getUserType())) {
			access.setAdmin(false);
			access.setSupervisor(false);
			access.setFinance(true);
			access.setOps(false);
			access.setAdvisor(false);
		} else if (UserType.OPERATIONS.equals(user.getUserType())) {
			access.setAdmin(false);
			access.setSupervisor(false);
			access.setFinance(false);
			access.setOps(true);
			access.setAdvisor(false);
		} else if (UserType.ADVISOR.equals(user.getUserType())) {
			access.setAdmin(false);
			access.setSupervisor(false);
			access.setFinance(false);
			access.setOps(false);
			access.setAdvisor(true);
		}
		return access;
	}

	public List<PcUser> getUsers() {
		List<PcUser> myUsers = new ArrayList<PcUser>();
		PcUser user = secSer.findLoggedInUser();
		if (secSer.isMerchantUser()) {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			List<MerchantUser> merUsers = merUserRepo.findByMerchantId(merchant.getId());
			for (MerchantUser merUser : merUsers) {
				PcUser myUser = userRepo.findById(merUser.getUserId()).get();
				if (!UserType.ADMIN.equals(myUser.getUserType())) {
					myUsers.add(myUser);
				}
			}
		} else {
			List<UserRole> superRoles = new ArrayList<UserRole>();
			if (UserType.ADMIN.equals(user.getUserType())) {
				superRoles = userRoleRepo.findByRole(Role.ROLE_PAYCR_SUPERVISOR);
			}
			List<UserRole> finRoles = userRoleRepo.findByRole(Role.ROLE_PAYCR_FINANCE);
			List<UserRole> opsRoles = userRoleRepo.findByRole(Role.ROLE_PAYCR_OPS);
			List<UserRole> advRoles = userRoleRepo.findByRole(Role.ROLE_PAYCR_ADVISOR);
			List<UserRole> userRoles = new ArrayList<UserRole>();
			userRoles.addAll(superRoles);
			userRoles.addAll(finRoles);
			userRoles.addAll(opsRoles);
			userRoles.addAll(advRoles);
			for (UserRole userRole : userRoles) {
				myUsers.add(userRole.getPcUser());
			}
		}
		return myUsers;
	}

	public void createUser(PcUser user) {
		logger.info("Create user request : {}", new Gson().toJson(user));
		userValidator.validate(user);
		user.setCreatedBy(secSer.findLoggedInUser().getEmail());
		Date timeNow = new Date();
		if (secSer.isMerchantUser()) {
			List<PcUser> existingUsers = getUsers();
			if (CommonUtil.isNotEmpty(existingUsers) && existingUsers.size() >= 10) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Not allowed to create more users");
			}
			user.setCreated(timeNow);
			user.setPassword(bcPassEncode.encode("password@123"));
			List<UserRole> userRoles = new ArrayList<UserRole>();
			UserRole userRole = new UserRole();
			userRole.setRole(getRoleForMerchantUserType(user.getUserType()));
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
			accessService.sendResetLink(user);
		} else {
			user.setCreated(timeNow);
			user.setPassword(bcPassEncode.encode("password@123"));
			List<UserRole> userRoles = new ArrayList<UserRole>();
			UserRole userRole = new UserRole();
			userRole.setRole(getRoleForAdminUserType(user.getUserType()));
			userRole.setPcUser(user);
			user.setUserRoles(userRoles);
			userRoles.add(userRole);
			user.setActive(true);
			userRepo.save(user);
		}
	}

	public void toggleUser(Integer userId) {
		PcUser user = userRepo.findById(userId).get();
		if (getUsers().contains(user)) {
			user.setActive(!user.isActive());
			userRepo.save(user);
		}
	}

	private Role getRoleForAdminUserType(UserType type) {
		if (UserType.SUPERVISOR.equals(type)) {
			return Role.ROLE_PAYCR_SUPERVISOR;
		} else if (UserType.FINANCE.equals(type)) {
			return Role.ROLE_PAYCR_FINANCE;
		} else if (UserType.OPERATIONS.equals(type)) {
			return Role.ROLE_PAYCR_OPS;
		} else if (UserType.ADVISOR.equals(type)) {
			return Role.ROLE_PAYCR_ADVISOR;
		} else {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid User Type specified");
		}
	}

	private Role getRoleForMerchantUserType(UserType type) {
		if (UserType.FINANCE.equals(type)) {
			return Role.ROLE_MERCHANT_FINANCE;
		} else if (UserType.OPERATIONS.equals(type)) {
			return Role.ROLE_MERCHANT_OPS;
		} else {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid User Type specified");
		}
	}

}
