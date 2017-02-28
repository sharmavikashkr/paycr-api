package com.payme.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.payme.common.data.domain.PmUser;
import com.payme.common.data.domain.UserRole;

@Service
public class UserRoleService {

	public String[] getUserRoles(PmUser user) {
		List<UserRole> userRoles = user.getUserRoles();
		String[] roles = new String[userRoles.size()];
		int i = 0;
		for (UserRole userRole : userRoles) {
			roles[i++] = userRole.getRole().name();
		}
		return roles;
	}

}
