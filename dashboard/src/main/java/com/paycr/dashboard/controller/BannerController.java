package com.paycr.dashboard.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
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

	@PreAuthorize(RoleUtil.ALL_AUTH)
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void getUser(@RequestParam("banner") MultipartFile banner, HttpServletResponse response) {
		try {
			banSer.uploadBanner(banner);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@RequestMapping("/admin/{bannerName}")
	public byte[] adminBanner(@PathVariable String bannerName, HttpServletResponse response) {
		try {
			return banSer.getAdminBanner(bannerName);
		} catch (IOException ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}

	@RequestMapping("/merchant/{bannerName}")
	public byte[] merchantBanner(@PathVariable String bannerName, HttpServletResponse response) {
		try {
			return banSer.getMerchantBanner(bannerName);
		} catch (IOException ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
			return null;
		}
	}
}
