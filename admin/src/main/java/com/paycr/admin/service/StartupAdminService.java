package com.paycr.admin.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.UserRole;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.type.Role;
import com.paycr.common.type.UserType;
import com.paycr.common.util.CommonUtil;

@Component
public class StartupAdminService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private NotificationRepository notiRepo;

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
	}

}
