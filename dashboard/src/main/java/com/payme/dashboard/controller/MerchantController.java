package com.payme.dashboard.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payme.common.data.domain.Merchant;
import com.payme.common.data.domain.MerchantPricing;
import com.payme.common.data.domain.MerchantUser;
import com.payme.common.data.domain.PmUser;
import com.payme.common.data.domain.Pricing;
import com.payme.common.data.domain.UserRole;
import com.payme.common.data.repository.MerchantRepository;
import com.payme.common.data.repository.MerchantUserRepository;
import com.payme.common.data.repository.PricingRepository;
import com.payme.common.data.repository.UserRepository;
import com.payme.common.type.PricingStatus;
import com.payme.common.type.Role;
import com.payme.common.util.DateUtil;
import com.payme.common.util.HmacSignerUtil;
import com.payme.common.util.RandomIdGenerator;
import com.payme.dashboard.service.UserService;
import com.payme.dashboard.validation.MerchantValidator;

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
	private PricingRepository priceRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Autowired
	private UserService userService;

	@Autowired
	private MerchantValidator merValidator;

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("new")
	public String newMerchant(@RequestBody Merchant merchant, HttpServletResponse response) {
		try {
			merValidator.validate(merchant);
		} catch (Exception ex) {
			response.setStatus(500);
			return ex.getMessage();
		}
		Date timeNow = new Date();
		String secretKey = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(timeNow.getTime()));
		String accessKey = secretKey + secretKey.toLowerCase() + secretKey.toUpperCase();
		accessKey = RandomIdGenerator.generateAccessKey(accessKey.toCharArray());
		merchant.setAccessKey(accessKey);
		merchant.setSecretKey(secretKey);
		merchant.setCreated(timeNow);
		merchant.setActive(true);

		Pricing pricing = priceRepo.findOne(merchant.getPricingId());

		List<MerchantPricing> merPricings = new ArrayList<MerchantPricing>();
		MerchantPricing merPricing = new MerchantPricing();
		merPricing.setCreated(timeNow);
		merPricing.setStartDate(timeNow);
		merPricing.setEndDate(DateUtil.getExpiry(timeNow, pricing.getDuration()));
		merPricing.setPricing(pricing);
		merPricing.setStatus(PricingStatus.ACTIVE);
		merPricing.setMerchant(merchant);
		merPricings.add(merPricing);
		merchant.setPricings(merPricings);

		merRepo.save(merchant);

		PmUser user = new PmUser();
		user.setCreated(timeNow);
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
		userService.sendResetLink(user);
		return "Merchant Created";
	}

}
