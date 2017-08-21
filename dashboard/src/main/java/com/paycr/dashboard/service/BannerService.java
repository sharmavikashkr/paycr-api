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
import com.paycr.common.service.SecurityService;

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
		if (merchant != null) {
			String bannerName = merchant.getAccessKey() + ".png";
			file = new File(server.getMerchantLocation() + bannerName);
			merchant.getInvoiceSetting().setBanner(bannerName);
			merRepo.save(merchant);
		} else {
			String bannerName = "paycr.png";
			file = new File(server.getAdminLocation() + "paycr.png");
			AdminSetting adset = adsetRepo.findAll().get(0);
			adset.setBanner(bannerName);
			adsetRepo.save(adset);
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(banner.getBytes());
		out.close();
	}

	public byte[] getAdminBanner(String bannerName) throws IOException {
		Path path = Paths.get(server.getAdminLocation() + bannerName + ".png");
		return Files.readAllBytes(path);
	}

	public byte[] getMerchantBanner(String bannerName) throws IOException {
		Path path = Paths.get(server.getMerchantLocation() + bannerName + ".png");
		return Files.readAllBytes(path);
	}

}
