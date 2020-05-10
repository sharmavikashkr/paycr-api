package com.paycr.merchant.controller;

import java.io.IOException;

import com.paycr.common.util.RoleUtil;
import com.paycr.merchant.service.BannerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/banner")
public class BannerController {

	@Autowired
	private BannerService banSer;

	@PreAuthorize(RoleUtil.MERCHANT_ADMIN_AUTH)
	@PostMapping("/upload")
	public void getUser(@RequestParam("banner") MultipartFile banner) throws Exception {
		banSer.uploadBanner(banner);
	}

	@GetMapping("/merchant/{accessKey}/{bannerName:.+}")
	public byte[] merchantBanner(@PathVariable String accessKey, @PathVariable String bannerName) throws IOException {
		return banSer.getMerchantBanner(accessKey, bannerName);
	}
}
