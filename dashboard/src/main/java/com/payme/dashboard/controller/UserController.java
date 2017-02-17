package com.payme.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.payme.common.data.domain.User;
import com.payme.common.data.repository.UserRepository;

@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public void createUser() {
		User user = new User();
		user.setName("Test Merchant");
		user.setEmail("merchant@payme.com");
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile("9970197591");
		user.setRoles(new String[] { "ROLE_MERCHANT" });
		userRepo.save(user);

		user = new User();
		user.setName("Test Admin");
		user.setEmail("admin@payme.com");
		user.setPassword(bcPassEncode.encode("password@123"));
		user.setMobile("9970197591");
		user.setRoles(new String[] { "ROLE_ADMIN" });
		userRepo.save(user);
	}

}
