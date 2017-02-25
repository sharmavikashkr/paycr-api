package com.payme.dashboard.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.payme.common.data.domain.PmUser;
import com.payme.common.data.domain.UserRole;
import com.payme.common.data.repository.UserRepository;
import com.payme.common.type.Role;
import com.payme.common.util.CommonUtil;

@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void createUser(HttpServletResponse response) throws IOException {
		Date timeNow = new Date();
		if (CommonUtil.isNotNull(userRepo.findByEmail("admin@payme.com"))) {
			return;
		}
		PmUser user = new PmUser();
		user.setCreated(timeNow);
		user.setName("Vikash Kumar");
		user.setEmail("admin@payme.com");
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile("9970197591");
		List<UserRole> userRoles = new ArrayList<UserRole>();
		UserRole userRole = new UserRole();
		userRole.setRole(Role.ROLE_ADMIN);
		userRole.setPmUser(user);
		userRoles.add(userRole);
		user.setUserRoles(userRoles);
		user.setActive(true);
		userRepo.save(user);
	}

}
