package com.paycr.dashboard.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.AdminSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.AdminSettingRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.Constants;

@Service
public class BannerService {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private AdminSettingRepository adsetRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private Server server;

	public void uploadBanner(MultipartFile banner) throws Exception {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		File file = null;
		String extension = validateBanner(banner);
		if (merchant != null) {
			String bannerName = merchant.getAccessKey() + extension;
			file = new File(server.getMerchantLocation() + "banner/" + bannerName);
			merchant.setBanner(bannerName);
			merRepo.save(merchant);
		} else {
			String bannerName = "paycr" + extension;
			file = new File(server.getAdminLocation() + "paycr" + extension);
			AdminSetting adset = adsetRepo.findAll().get(0);
			adset.setBanner(bannerName);
			adsetRepo.save(adset);
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(banner.getBytes());
		out.close();
	}

	private String validateBanner(MultipartFile banner) {
		String contentType = banner.getContentType().toLowerCase();
		if (!contentType.contains("png") && !contentType.contains("jpeg")) {
			throw new PaycrException(Constants.FAILURE, "Invalid banner file type");
		}
		if (contentType.contains("png")) {
			return ".png";
		} else if (contentType.contains("jpeg")) {
			return ".jpg";
		}
		return null;
	}

	public byte[] getAdminBanner(String bannerName) throws IOException {
		Path path = Paths.get(server.getAdminLocation() + bannerName);
		return Files.readAllBytes(path);
	}

	public byte[] getMerchantBanner(String bannerName) throws IOException {
		Path path = Paths.get(server.getMerchantLocation() + "banner/" + bannerName);
		return Files.readAllBytes(path);
	}

}
