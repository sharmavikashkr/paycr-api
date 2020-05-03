package com.paycr.dashboard.controller;

import java.util.ArrayList;
import java.util.List;

import com.paycr.common.data.domain.PcUser;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.ReportType;
import com.paycr.common.type.TimeRange;
import com.paycr.common.type.UserType;
import com.paycr.common.util.RoleUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaticController {

	@Autowired
	private SecurityService secSer;

	/*@RequestMapping("/html/{folder}/{file}")
	public ModelAndView getTemplate(@PathVariable String folder, @PathVariable String file,
			@RequestParam("access_token") String accessToken, HttpServletRequest request, HttpServletResponse response)
					throws IOException {
		PcUser user = secSer.findLoggedInUser(accessToken);
		if (CommonUtil.isNull(user)) {
			response.sendRedirect("/login");
		}
		boolean isPaycr = secSer.isPaycrUser(accessToken);
		if ("admin".equals(folder) && !isPaycr) {
			response.sendRedirect("/login");
		}
		if ("merchant".equals(folder) && isPaycr) {
			response.sendRedirect("/adminlogin");
		}
		return new ModelAndView("html/" + folder + "/" + file);
	}

	@RequestMapping("/html/{folder}/{subfolder}/{file}")
	public ModelAndView getSubTemplate(@PathVariable String folder, @PathVariable String subfolder,
			@PathVariable String file, @RequestParam("access_token") String accessToken, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PcUser user = secSer.findLoggedInUser(accessToken);
		if (CommonUtil.isNull(user)) {
			response.sendRedirect("/login");
		}
		boolean isPaycr = secSer.isPaycrUser(accessToken);
		if ("admin".equals(folder) && !isPaycr) {
			response.sendRedirect("/login");
		}
		if ("merchant".equals(folder) && isPaycr) {
			response.sendRedirect("/adminlogin");
		}
		return new ModelAndView("html/" + folder + "/" + subfolder + "/" + file);
	}*/

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping("/enum/{type}")
	public List<String> getEnum(@PathVariable String type) {
		List<String> enumList = new ArrayList<>();
		if ("paymodes".equals(type)) {
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
		} else if ("reporttypes".equals(type)) {
			for (ReportType repType : ReportType.values()) {
				enumList.add(repType.name());
			}
		}
		return enumList;
	}

}
