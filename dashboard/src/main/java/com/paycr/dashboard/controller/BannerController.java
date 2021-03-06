package com.paycr.dashboard.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.BannerService;

@RestController
@RequestMapping("/banner")
public class BannerController {

	@Autowired
	private BannerService banSer;

	@PreAuthorize(RoleUtil.MERCHANT_ADMIN_AUTH)
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void getUser(@RequestParam("banner") MultipartFile banner) throws Exception {
		banSer.uploadBanner(banner);
	}

	@RequestMapping("/merchant/{accessKey}/{bannerName:.+}")
	public byte[] merchantBanner(@PathVariable String accessKey, @PathVariable String bannerName) throws IOException {
		return banSer.getMerchantBanner(accessKey, bannerName);
	}
}
