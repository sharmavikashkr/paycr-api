package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.domain.SubscriptionMode;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.SubscriptionModeRepository;
import com.paycr.common.data.repository.SubscriptionRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.Currency;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PricingStatus;
import com.paycr.common.type.Role;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.paycr.dashboard.validation.MerchantValidator;
import com.paycr.dashboard.validation.PricingValidator;

@Service
public class AdminService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private MerchantPricingRepository merPriRepo;

	@Autowired
	private SubscriptionRepository subsRepo;

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

	@Autowired
	private SubscriptionModeRepository subsModeRepo;

	@Autowired
	private MerchantValidator merValidator;

	@Autowired
	private PricingValidator pricingValidator;

	@Autowired
	private PricingRepository pricingRepo;

	public void createMerchant(Merchant merchant) {
		merValidator.validate(merchant);
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

		Pricing pricing = priceRepo.findOne(1);

		SubscriptionMode subsMode = subsModeRepo.findByActiveAndPayMode(true, PayMode.CASH);
		Subscription subs = new Subscription();
		subs.setAmount(pricing.getRate());
		subs.setCurrency(Currency.INR);
		subs.setCreated(timeNow);
		subs.setPricing(pricing);
		subs.setStatus("SUCCESS");
		subs.setSubscriptionCode("OFFLINE");
		subs.setSubscriptionMode(subsMode);
		subsRepo.save(subs);

		PaymentSetting paymentSetting = new PaymentSetting();
		paymentSetting.setRzpMerchantId("");
		paymentSetting.setRzpKeyId("");
		paymentSetting.setRzpSecretId("");
		merchant.setPaymentSetting(paymentSetting);

		InvoiceSetting invoiceSetting = new InvoiceSetting();
		invoiceSetting.setSendEmail(true);
		invoiceSetting.setSendSms(false);
		invoiceSetting.setExpiryDays(7);
		invoiceSetting.setTax(0.0F);
		merchant.setInvoiceSetting(invoiceSetting);
		merRepo.save(merchant);
		
		MerchantPricing merPricing = new MerchantPricing();
		merPricing.setCreated(timeNow);
		merPricing.setStartDate(timeNow);
		merPricing.setEndDate(DateUtil.getExpiry(timeNow, pricing.getDuration()));
		merPricing.setPricing(pricing);
		merPricing.setStatus(PricingStatus.ACTIVE);
		merPricing.setMerchant(merchant);
		merPricing.setSubscription(subs);
		merPriRepo.save(merPricing);

		subs.setMerchant(merchant);
		subsRepo.save(subs);

		PcUser user = new PcUser();
		user.setCreated(timeNow);
		user.setName("Admin");
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

	public List<Notification> getNotifications(PcUser user) {
		Pageable topFour = new PageRequest(0, 4);
		List<Notification> notices = notiRepo.findByUserIdAndMerchantIdOrderByIdDesc(user.getId(), null, topFour);
		for (Notification notice : notices) {
			notice.setCreatedStr(DateUtil.getDashboardDate(notice.getCreated()));
		}
		return notices;
	}

	public void createPricing(Pricing pricing) {
		pricingValidator.validate(pricing);
		pricingRepo.save(pricing);
	}

	public void togglePricing(Integer pricingId) {
		Pricing pri = pricingRepo.findOne(pricingId);
		if (pri.isActive()) {
			pri.setActive(false);
		} else {
			pri.setActive(true);
		}
		pricingRepo.save(pri);
	}

}
