package com.paycr.merchant.service;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.awss3.AwsS3Folder;
import com.paycr.common.awss3.AwsS3Service;
import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.PaycrUtil;

@Service
public class BannerService {

	private static final Logger logger = LoggerFactory.getLogger(BannerService.class);

	private int maxUploadSizeInMb = 2 * 1024 * 1024;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private Server server;

	@Autowired
	private AwsS3Service awsS3Ser;

	public void uploadBanner(MultipartFile banner) throws Exception {
		logger.info("Upload new banner request");
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		File file = null;
		String extension = validateBanner(banner);
		String bannerName = merchant.getAccessKey() + extension;
		file = new File(server.getMerchantLocation() + bannerName);
		PaycrUtil.saveFile(file, banner);
		awsS3Ser.saveFile(merchant.getAccessKey().concat("/").concat(AwsS3Folder.MERCHANT), file);
		merchant.setBanner(bannerName);
		merRepo.save(merchant);

	}

	private String validateBanner(MultipartFile banner) {
		if (maxUploadSizeInMb < banner.getSize()) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Banner size limit 2MBs");
		}
		String contentType = banner.getContentType().toLowerCase();
		if (!contentType.contains("png") && !contentType.contains("jpeg")) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid banner file type");
		}
		if (contentType.contains("png")) {
			return ".png";
		} else if (contentType.contains("jpeg")) {
			return ".jpg";
		}
		return null;
	}

	public byte[] getMerchantBanner(String accessKey, String bannerName) throws IOException {
		return awsS3Ser.getFile(accessKey.concat("/").concat(AwsS3Folder.MERCHANT), bannerName);
	}

}
