package com.payme.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.payme.common.data.domain.Role;
import com.payme.common.data.domain.User;
import com.payme.common.data.domain.UserRole;
import com.payme.common.data.repository.RoleRepository;
import com.payme.common.data.repository.UserRepository;
import com.payme.common.data.repository.UserRoleRepository;

@RestController
public class DashboardController {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private UserRoleRepository userRoleRepo;
	
	@Autowired
	private SecurityService secSer;

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("html/index");
	}

	//@Secured({ "ROLE_MERCHANT" })
	@RequestMapping("/dashboard")
	public ModelAndView dashboard() {
		User user = secSer.findLoggedInUser();
		ModelAndView mv = new ModelAndView("html/dashboard");
		mv.addObject("email", user.getEmail());
		return mv;
	}

	@RequestMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("html/login");
	}

	@RequestMapping(value = "/createuser", method = RequestMethod.GET)
	public void createUser() {
		Role role = new Role();
		role.setRole("ROLE_MERCHANT");
		roleRepo.save(role);
		User user = new User();
		user.setEmail("user@email.com");
		user.setPassword("password");
		userRepo.save(user);
		UserRole userRole = new UserRole();
		userRole.setRoleId(role.getId());
		userRole.setUserId(user.getId());
		userRoleRepo.save(userRole);
	}
}
