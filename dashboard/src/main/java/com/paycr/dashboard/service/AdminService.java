package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.PricingStatus;
import com.paycr.common.type.Role;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.RandomIdGenerator;

@Service
public class AdminService {

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
	private NotificationRepository notiRepo;

	public void createMerchant(Merchant merchant) {

		Date timeNow = new Date();
		String secretKey = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(timeNow.getTime()));
		String accessKey = secretKey + secretKey.toLowerCase() + secretKey.toUpperCase();
		do {
			accessKey = RandomIdGenerator.generateAccessKey(accessKey.toCharArray());
		} while (merRepo.findByAccessKey(accessKey) != null);
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

		MerchantSetting setting = new MerchantSetting();
		setting.setSendEmail(true);
		setting.setSendSms(false);
		setting.setExpiryDays(7);
		setting.setRzpMerchantId("");
		setting.setRzpKeyId("");
		setting.setRzpSecretId("");
		setting.setMerchant(merchant);
		merchant.setSetting(setting);

		merRepo.save(merchant);

		PcUser user = new PcUser();
		user.setCreated(timeNow);
		user.setName(merchant.getAdminName());
		user.setEmail(merchant.getEmail());
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile(merchant.getMobile());
		List<UserRole> userRoles = new ArrayList<UserRole>();
		UserRole userRole = new UserRole();
		userRole.setRole(Role.ROLE_MERCHANT);
		userRole.setPcUser(user);
		user.setUserRoles(userRoles);
		userRoles.add(userRole);
		user.setActive(true);
		userRepo.save(user);

		MerchantUser merUser = new MerchantUser();
		merUser.setMerchantId(merchant.getId());
		merUser.setUserId(user.getId());
		merUserRepo.save(merUser);
		userService.sendResetLink(user);

		Notification noti = new Notification();
		noti.setMerchantId(merchant.getId());
		noti.setMessage("Take a tour of the features..");
		noti.setSubject("Welcome to Paycr");
		noti.setCreated(timeNow);
		noti.setRead(false);
		notiRepo.save(noti);
	}

}
