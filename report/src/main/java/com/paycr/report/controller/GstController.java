package com.paycr.report.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.service.SecurityService;
import com.paycr.report.service.GstService;

@RestController
@RequestMapping("/gst")
public class GstController {

	@Autowired
	private GstService gstSer;

	@Autowired
	private SecurityService secSer;

	@RequestMapping("/gstr1/{month}")
	public Gstr1Report gstr1(@PathVariable String month, HttpServletResponse httpResponse) throws Exception {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return gstSer.loadGstr1Report(merchant, month);
	}

}
