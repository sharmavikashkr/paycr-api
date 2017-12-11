package com.paycr.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.OfflineSubscription;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.AdminSetting;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.AdminSettingRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.Role;
import com.paycr.common.type.UserType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
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
	private MerchantUserRepository merUserRepo;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Autowired
	private LoginService userService;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private MerchantValidator merValidator;

	@Autowired
	private PricingValidator pricingValidator;

	@Autowired
	private PricingRepository pricingRepo;

	@Autowired
	private AdminSettingRepository adsetRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Autowired
	private SubscriptionService subsService;

	public void createMerchant(Merchant merchant, String createdBy) {
		merValidator.validate(merchant);
		boolean enableWelcome = merchant.isEnableWelcome();
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
		invoiceSetting.setEmailNote("Thankyou for avaling our service");
		invoiceSetting.setEmailSubject("Invoice for your order");
		invoiceSetting.setExpiryDays(7);
		merchant.setInvoiceSetting(invoiceSetting);
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
		userService.sendResetLink(user);

		Notification noti = new Notification();
		noti.setMerchantId(merchant.getId());
		noti.setMessage("Happy Invoicing..");
		noti.setSubject("Welcome to Paycr");
		noti.setCreated(timeNow);
		noti.setRead(false);
		notiRepo.save(noti);

		if (enableWelcome) {
			Pricing welcomePricing = pricingRepo.findByNameAndActive("WELCOME", true);
			if (welcomePricing == null) {
				return;
			}
			OfflineSubscription offSubs = new OfflineSubscription();
			offSubs.setMerchantId(merchant.getId());
			offSubs.setPricingId(welcomePricing.getId());
			offSubs.setPaymentRefNo("auto-enabled");
			offSubs.setPayMode(PayMode.CASH);
			offSubs.setQuantity(1);
			subsService.offlineSubscription(offSubs);
		}
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

	public AdminSetting getSetting() {
		return adsetRepo.findAll().get(0);
	}

	public void saveSetting(AdminSetting setting) {
		AdminSetting adset = adsetRepo.findAll().get(0);
		adset.setBanner(setting.getBanner());
		adset.setTax(setting.getTax());
		adset.setGstin(setting.getGstin());
		adset.setHsnsac(setting.getHsnsac());
		PaymentSetting payset = adset.getPaymentSetting();
		payset.setRzpMerchantId(setting.getPaymentSetting().getRzpMerchantId());
		payset.setRzpKeyId(setting.getPaymentSetting().getRzpKeyId());
		payset.setRzpSecretId(setting.getPaymentSetting().getRzpSecretId());
		adsetRepo.save(adset);
	}

	public void saveAddress(Address newAddr) {
		AdminSetting adset = adsetRepo.findAll().get(0);
		Address exstAddr = adset.getAddress();
		if (CommonUtil.isNull(exstAddr)) {
			exstAddr = new Address();
		}
		exstAddr.setAddressLine1(newAddr.getAddressLine1());
		exstAddr.setAddressLine1(newAddr.getAddressLine2());
		exstAddr.setCity(newAddr.getCity());
		exstAddr.setDistrict(newAddr.getCity());
		exstAddr.setState(newAddr.getState());
		exstAddr.setPincode(newAddr.getPincode());
		exstAddr.setCountry(newAddr.getCountry());
		adset.setAddress(exstAddr);
		adsetRepo.save(adset);
	}

	public void newTaxMaster(TaxMaster tax) {
		if (CommonUtil.isNull(tax) || CommonUtil.isNull(tax.getName()) || CommonUtil.isNull(tax.getValue())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Tax request");
		}
		if (tax.isChild()) {
			if (CommonUtil.isNull(tax.getParentTaxId())) {
				throw new PaycrException(Constants.FAILURE, "Child tax must have a parent");
			}
			TaxMaster parent = taxMRepo.findOne(tax.getParentTaxId());
			if (CommonUtil.isNull(parent) || !parent.isActive()) {
				throw new PaycrException(Constants.FAILURE, "Parent tax not found");
			}
			tax.setTaxParent(parent);
		}
		tax.setActive(true);
		taxMRepo.save(tax);
	}

}
