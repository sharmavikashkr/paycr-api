package com.paycr.merchant.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	@PostMapping("/new")
	public void newAsset(@Valid @RequestBody Asset asset) {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		astSer.newAsset(asset, merchant, user.getEmail());
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH + " && hasPermission('ASSET', #assetId)")
	@PutMapping("/update/{assetId}")
	public void updateAsset(@RequestBody Asset asset, @PathVariable Integer assetId) {
		astSer.updateAsset(asset, assetId);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@PostMapping(value = "/bulk/upload")
	public void uploadAsset(@RequestParam("asset") MultipartFile asset) throws IOException {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		astSer.uploadAsset(asset, merchant, user.getEmail());
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@GetMapping("/bulk/upload/format")
	public void downloadFormat(HttpServletResponse response) throws Exception {
		String content = "Code1,Name1,Rate1,HSN/SAC1,Description1\r\nCode2,Name2,Rate2,HSN/SAC2,Description2";
		response.setHeader("Content-Disposition", "attachment; filename=\"bulkAsset.csv\"");
		response.setContentType("application/csv");
		response.getOutputStream().write(content.getBytes());
		response.setContentLength(content.getBytes().length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@GetMapping("/bulk/uploads/all")
	public List<BulkAssetUpload> uploadAsset() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return astSer.getUploads(merchant);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@GetMapping("/bulk/download/{accessKey}/{filename:.+}")
	public byte[] downloadFile(@PathVariable String accessKey, @PathVariable String filename) throws IOException {
		return astSer.downloadFile(accessKey, filename);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH + " && hasPermission('ASSET', #assetId)")
	@GetMapping("/stats/{assetId}")
	public AssetStats updateAsset(@PathVariable Integer assetId) {
		return astSer.getStats(assetId);
	}
}
