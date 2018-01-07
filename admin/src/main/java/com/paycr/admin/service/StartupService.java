package com.paycr.admin.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.AdminSetting;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.AdminSettingRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.PricingType;
import com.paycr.common.type.Role;
import com.paycr.common.type.UserType;
import com.paycr.common.util.CommonUtil;

@Component
public class StartupService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private AdminSettingRepository adsetRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Autowired
	private PricingRepository priRepo;

	public void createSuperAdmin() {
		Date timeNow = new Date();
		if (CommonUtil.isNotNull(userRepo.findByEmail("admin@paycr.in"))) {
			return;
		}
		PcUser user = new PcUser();
		user.setCreated(timeNow);
		user.setName("Paycr Admin");
		user.setEmail("admin@paycr.in");
		user.setPassword(new BCryptPasswordEncoder().encode("password@123"));
		user.setMobile("9999999999");
		user.setUserType(UserType.ADMIN);
		user.setCreatedBy("SYSTEM");
		List<UserRole> userRoles = new ArrayList<UserRole>();
		UserRole userRole = new UserRole();
		userRole.setRole(Role.ROLE_PAYCR);
		userRole.setPcUser(user);
		userRoles.add(userRole);
		user.setUserRoles(userRoles);
		user.setActive(true);
		userRepo.save(user);

		Notification noti = new Notification();
		noti.setUserId(user.getId());
		noti.setMessage("Hope you manage the product well :)");
		noti.setSubject("Welcome to Paycr");
		noti.setCreated(timeNow);
		noti.setRead(false);
		notiRepo.save(noti);

		PaymentSetting payset = new PaymentSetting();
		payset.setRzpMerchantId("");
		payset.setRzpKeyId("");
		payset.setRzpSecretId("");

		AdminSetting adset = new AdminSetting();
		adset.setPaymentSetting(payset);

		adsetRepo.save(adset);
	}

	public void createWelcomePricing() {
		Pricing welcomePri = priRepo.findByCodeAndActive("PPC-P-001", true);
		if (CommonUtil.isNotNull(welcomePri)) {
			return;
		}
		welcomePri = new Pricing();
		welcomePri.setCode("PPC-P-001");
		welcomePri.setActive(true);
		welcomePri.setCreated(new Date());
		welcomePri.setDescription("Welcome Plan");
		welcomePri.setDuration(90);
		welcomePri.setName("WELCOME");
		welcomePri.setRate(new BigDecimal(0));
		welcomePri.setLimit(1000);
		welcomePri.setType(PricingType.PUBLIC);
		TaxMaster noTax = createNoTaxMaster();
		welcomePri.setTax(noTax);
		priRepo.save(welcomePri);
	}

	private TaxMaster createNoTaxMaster() {
		TaxMaster noTax = taxMRepo.findByName("NO_TAX");
		if (CommonUtil.isNotNull(noTax)) {
			return noTax;
		}
		noTax = new TaxMaster();
		noTax.setActive(true);
		noTax.setChild(false);
		noTax.setName("NO_TAX");
		noTax.setValue(0F);
		return taxMRepo.save(noTax);
	}

}
