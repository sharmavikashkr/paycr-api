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
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayStatus;
import com.paycr.common.type.PayType;
import com.paycr.common.type.RecurrType;
import com.paycr.common.type.TimeRange;
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
	@RequestMapping("/enum/{type}")
	public List<String> getEnum(@PathVariable String type) {
		List<String> enumList = new ArrayList<String>();
		if ("providers".equals(type)) {
			for (ParamValueProvider provider : ParamValueProvider.values()) {
				enumList.add(provider.name());
			}
		} else if ("paymodes".equals(type)) {
			for (PayMode payMode : PayMode.values()) {
				enumList.add(payMode.name());
			}
		} else if ("usertypes".equals(type)) {
			boolean isMerchant = secSer.isMerchantUser();
			PcUser user = secSer.findLoggedInUser();
			if (isMerchant) {
				enumList.add(UserType.FINANCE.name());
				enumList.add(UserType.OPERATIONS.name());
			} else {
				if (UserType.ADMIN.equals(user.getUserType())) {
					enumList.add(UserType.SUPERVISOR.name());
				}
				enumList.add(UserType.FINANCE.name());
				enumList.add(UserType.OPERATIONS.name());
				enumList.add(UserType.ADVISOR.name());
			}
		} else if ("timeranges".equals(type)) {
			for (TimeRange timeRange : TimeRange.values()) {
				enumList.add(timeRange.name());
			}
		} else if ("paytypes".equals(type)) {
			for (PayType payType : PayType.values()) {
				enumList.add(payType.name());
			}
		} else if ("invoicestatuses".equals(type)) {
			for (InvoiceStatus status : InvoiceStatus.values()) {
				enumList.add(status.name());
			}
		} else if ("paystatuses".equals(type)) {
			for (PayStatus status : PayStatus.values()) {
				enumList.add(status.name());
			}
		} else if ("invoicetypes".equals(type)) {
			for (InvoiceType invType : InvoiceType.values()) {
				enumList.add(invType.name());
			}
		} else if ("recurrtypes".equals(type)) {
			for (RecurrType recType : RecurrType.values()) {
				enumList.add(recType.name());
			}
		}
		return enumList;
	}

}
