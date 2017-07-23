package com.paycr.dashboard.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.PcUser;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.UserType;
import com.paycr.common.util.RoleUtil;

@RestController
public class StaticController {

	@Autowired
	private SecurityService secSer;

	@RequestMapping("/html/{folder}/{file}")
	public ModelAndView getTemplate(@PathVariable String folder, @PathVariable String file, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String token = null;
		if (request.getCookies() == null) {
			response.sendRedirect("/login");
		}
		for (Cookie cookie : request.getCookies()) {
			if ("access_token".equals(cookie.getName())) {
				token = cookie.getValue();
			}
		}
		if (token == null) {
			response.sendRedirect("/login");
		}
		PcUser user = secSer.findLoggedInUser(token);
		if (user == null) {
			response.sendRedirect("/login");
		}
		return new ModelAndView("html/" + folder + "/" + file);
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/enum/providers")
	public List<String> getParamProviders() {
		List<String> providers = new ArrayList<String>();
		for (ParamValueProvider provider : ParamValueProvider.values()) {
			providers.add(provider.name());
		}
		return providers;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/enum/paymodes")
	public List<String> getPayModes() {
		List<String> payModes = new ArrayList<String>();
		for (PayMode payMode : PayMode.values()) {
			payModes.add(payMode.name());
		}
		return payModes;
	}

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/enum/usertypes")
	public List<String> getUserTypes() {
		boolean isMerchant = secSer.isMerchantUser();
		PcUser user = secSer.findLoggedInUser();
		List<String> userTypes = new ArrayList<String>();
		if (isMerchant) {
			userTypes.add(UserType.FINANCE.name());
			userTypes.add(UserType.OPERATIONS.name());
		} else {
			if (UserType.ADMIN.equals(user.getUserType())) {
				userTypes.add(UserType.SUPERVISOR.name());
			}
			userTypes.add(UserType.FINANCE.name());
			userTypes.add(UserType.OPERATIONS.name());
			userTypes.add(UserType.ADVISOR.name());
		}
		return userTypes;
	}

}
