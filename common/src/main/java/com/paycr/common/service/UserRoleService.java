package com.paycr.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.UserRole;

@Service
public class UserRoleService {

	public String[] getUserRoles(PcUser user) {
		List<UserRole> userRoles = user.getUserRoles();
		String[] roles = new String[userRoles.size()];
		int i = 0;
		for (UserRole userRole : userRoles) {
			roles[i++] = userRole.getRole().name();
		}
		return roles;
	}

}
