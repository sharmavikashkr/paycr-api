package com.paycr.merchant.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.AssetStats;
import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.BulkAssetUpload;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.merchant.service.AssetService;

@RestController
@RequestMapping("/asset")
public class AssetController {

	@Autowired
	private AssetService astSer;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/new")
	public void newAsset(@RequestBody Asset asset, HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			astSer.newAsset(asset, merchant, user.getEmail());
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/update/{assetId}")
	public void updateAsset(@RequestBody Asset asset, @PathVariable Integer assetId, HttpServletResponse response) {
		try {
			astSer.updateAsset(asset, assetId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/upload", method = RequestMethod.POST)
	public void uploadAsset(@RequestParam("asset") MultipartFile asset, HttpServletResponse response) {
		try {
			PcUser user = secSer.findLoggedInUser();
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			astSer.uploadAsset(asset, merchant, user.getEmail());
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@RequestMapping("/bulk/upload/format")
	public void downloadFormat(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "Code1,Name1,Rate1,HSN/SAC1,Description1\r\nCode2,Name2,Rate2,HSN/SAC2,Description2";
		response.setHeader("Content-Disposition", "attachment; filename=\"bulkAsset.csv\"");
		response.setContentType("application/csv");
		InputStream is = new ByteArrayInputStream(content.getBytes());
		IOUtils.copy(is, response.getOutputStream());
		response.setContentLength(content.getBytes().length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/uploads/all", method = RequestMethod.GET)
	public List<BulkAssetUpload> uploadAsset(HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			return astSer.getUploads(merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}

	@RequestMapping(value = "/bulk/download/{filename:.+}", method = RequestMethod.GET)
	public byte[] downloadFile(@PathVariable String filename, HttpServletResponse response) {
		try {
			return astSer.downloadFile(filename);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/stats/{assetId}")
	public AssetStats updateAsset(@PathVariable Integer assetId, HttpServletResponse response) {
		try {
			return astSer.getStats(assetId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}
}
