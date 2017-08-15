package com.paycr.dashboard.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.Access;
import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.bean.StatsRequest;
import com.paycr.common.bean.StatsResponse;
import com.paycr.common.data.dao.InvoiceDao;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.data.repository.UserRoleRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.PayType;
import com.paycr.common.type.Role;
import com.paycr.common.type.UserType;
import com.paycr.common.util.Constants;
import com.paycr.dashboard.validation.UserValidator;

@Service
public class CommonService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PricingRepository priceRepo;

	@Autowired
	private UserRoleRepository userRoleRepo;

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
	private InvoiceRepository invRepo;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private PaymentRepository payRepo;

	@Autowired
	private InvoiceDao invDao;

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

	public List<Pricing> getPricings() {
		List<Pricing> pricings = priceRepo.findAll();
		return pricings;
	}

	public List<Notification> getNotifications() {
		Pageable topFour = new PageRequest(0, 4);
		if (secSer.isMerchantUser()) {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			List<Notification> notices = notiRepo.findByUserIdAndMerchantIdOrderByIdDesc(null, merchant.getId(),
					topFour);
			return notices;
		} else {
			PcUser user = secSer.findLoggedInUser();
			List<Notification> notices = notiRepo.findByUserIdAndMerchantIdOrderByIdDesc(user.getId(), null, topFour);
			return notices;
		}
	}

	public List<PcUser> getUsers() {
		List<PcUser> myUsers = new ArrayList<PcUser>();
		PcUser user = secSer.findLoggedInUser();
		if (secSer.isMerchantUser()) {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			List<MerchantUser> merUsers = merUserRepo.findByMerchantId(merchant.getId());
			for (MerchantUser merUser : merUsers) {
				PcUser myUser = userRepo.findOne(merUser.getUserId());
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
		userValidator.validate(user);
		user.setCreatedBy(secSer.findLoggedInUser().getEmail());
		Date timeNow = new Date();
		if (secSer.isMerchantUser()) {
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
			userService.sendResetLink(user);
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
			throw new PaycrException(Constants.FAILURE, "Invalid User Type specified");
		}
	}

	private Role getRoleForMerchantUserType(UserType type) {
		if (UserType.FINANCE.equals(type)) {
			return Role.ROLE_MERCHANT_FINANCE;
		} else if (UserType.OPERATIONS.equals(type)) {
			return Role.ROLE_MERCHANT_OPS;
		} else {
			throw new PaycrException(Constants.FAILURE, "Invalid User Type specified");
		}
	}

	public StatsResponse loadDashboard(StatsRequest request) {
		if (request == null || request.getCreatedFrom() == null || request.getCreatedTo() == null) {
			throw new PaycrException(Constants.FAILURE, "Invalid Request");
		}
		StatsResponse response = new StatsResponse();
		SearchInvoiceRequest searchReq = new SearchInvoiceRequest();
		searchReq.setCreatedFrom(request.getCreatedFrom());
		searchReq.setCreatedTo(request.getCreatedTo());
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		searchReq.setInvoiceStatus(InvoiceStatus.PAID);
		List<Invoice> paidInvs = invDao.findInvoices(searchReq, merchant);
		searchReq.setInvoiceStatus(InvoiceStatus.UNPAID);
		List<Invoice> unpaidInvs = invDao.findInvoices(searchReq, merchant);
		searchReq.setInvoiceStatus(InvoiceStatus.EXPIRED);
		List<Invoice> expiredInvs = invDao.findInvoices(searchReq, merchant);
		searchReq.setInvoiceStatus(InvoiceStatus.DECLINED);
		List<Invoice> declinedInvs = invDao.findInvoices(searchReq, merchant);
		List<Payment> refundPays = new ArrayList<>();
		if (merchant == null) {
			refundPays = payRepo.findPays(request.getCreatedFrom(), request.getCreatedTo(), PayType.REFUND);
		} else {
			refundPays = payRepo.findPaysForMerchant(request.getCreatedFrom(), request.getCreatedTo(), PayType.REFUND,
					merchant);
		}

		response.setPaidInvs(paidInvs);
		response.setPaidInvSum(getTotalInvAmount(paidInvs));
		response.setUnpaidInvs(unpaidInvs);
		response.setUnpaidInvSum(getTotalInvAmount(unpaidInvs));
		response.setExpiredInvs(expiredInvs);
		response.setExpiredInvSum(getTotalInvAmount(expiredInvs));
		response.setDeclinedInvs(declinedInvs);
		response.setDeclinedInvSum(getTotalInvAmount(declinedInvs));
		response.setRefundPays(refundPays);
		response.setRefundPaySum(getTotalPayAmount(refundPays));

		return response;
	}

	private BigDecimal getTotalInvAmount(List<Invoice> invs) {
		BigDecimal total = new BigDecimal(0);
		for (Invoice inv : invs) {
			total = total.add(inv.getPayAmount());
		}
		return total;
	}

	private BigDecimal getTotalPayAmount(List<Payment> pays) {
		BigDecimal total = new BigDecimal(0);
		for (Payment pay : pays) {
			total = total.add(pay.getAmount());
		}
		return total;
	}

}
