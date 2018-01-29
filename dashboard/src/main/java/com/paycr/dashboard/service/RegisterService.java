package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.OfflineSubscription;
import com.paycr.common.data.domain.GstSetting;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.FilingPeriod;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.Role;
import com.paycr.common.type.UserType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.paycr.dashboard.validation.MerchantValidator;

@Service
public class RegisterService {

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

	@Autowired
	private AccessService accessService;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private MerchantValidator merValidator;

	@Autowired
	private PricingRepository pricingRepo;

	@Autowired
	private SubscriptionService subsService;

	public void createMerchant(Merchant merchant, String createdBy) {
		merValidator.validate(merchant);
		Date timeNow = new Date();
		String secretKey = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(timeNow.getTime()));
		String accessKey = secretKey + secretKey.toLowerCase() + secretKey.toUpperCase();
		do {
			accessKey = RandomIdGenerator.generateAccessKey(accessKey.toCharArray());
		} while (CommonUtil.isNotNull(merRepo.findByAccessKey(accessKey)));
		merchant.setAccessKey(accessKey);
		merchant.setSecretKey(secretKey);
		merchant.setCreated(timeNow);
		merchant.setActive(true);

		PaymentSetting paymentSetting = new PaymentSetting();
		paymentSetting.setRzpMerchantId("");
		paymentSetting.setRzpKeyId("");
		paymentSetting.setRzpSecretId("");
		merchant.setPaymentSetting(paymentSetting);

		InvoiceSetting invoiceSetting = new InvoiceSetting();
		invoiceSetting.setSendEmail(true);
		invoiceSetting.setSendSms(false);
		invoiceSetting.setAddItems(true);
		invoiceSetting.setEmailPdf(false);
		invoiceSetting.isCcMe();
		invoiceSetting.setRefundCreditNote(true);
		invoiceSetting.setEmailNote("Thankyou for avaling our service");
		invoiceSetting.setEmailSubject("Invoice for your order");
		invoiceSetting.setExpiryDays(7);
		merchant.setInvoiceSetting(invoiceSetting);

		GstSetting gstSetting = new GstSetting();
		gstSetting.setFilingPeriod(FilingPeriod.MONTHLY);
		gstSetting.setExpPaid(true);
		gstSetting.setExpUnpaid(true);
		gstSetting.setInvCreated(true);
		gstSetting.setInvDeclined(false);
		gstSetting.setInvExpired(false);
		gstSetting.setInvPaid(true);
		gstSetting.setInvUnpaid(true);
		merchant.setGstSetting(gstSetting);

		merRepo.save(merchant);

		PcUser user = new PcUser();
		user.setCreated(timeNow);
		user.setName("Admin");
		user.setEmail(merchant.getEmail());
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile(merchant.getMobile());
		user.setUserType(UserType.ADMIN);
		user.setCreatedBy(createdBy);
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
		accessService.sendResetLink(user);

		Notification noti = new Notification();
		noti.setMerchantId(merchant.getId());
		noti.setMessage("Happy Invoicing..");
		noti.setSubject("Welcome to Paycr");
		noti.setCreated(timeNow);
		noti.setRead(false);
		notiRepo.save(noti);

		Pricing welcomePricing = pricingRepo.findByCodeAndActive("PPC-P-001", true);
		if (CommonUtil.isNull(welcomePricing)) {
			return;
		}
		OfflineSubscription offSubs = new OfflineSubscription();
		offSubs.setMerchantId(merchant.getId());
		offSubs.setPricingId(welcomePricing.getId());
		offSubs.setPaymentRefNo("auto-enabled");
		offSubs.setPayMode(PayMode.CASH);
		offSubs.setMethod(PayMode.CASH.name());
		offSubs.setQuantity(1);
		subsService.offlineSubscription(offSubs);
	}

}
