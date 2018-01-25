package com.paycr.merchant.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paycr.common.bean.InventoryStats;
import com.paycr.common.data.domain.BulkInventoryUpload;
import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;
import com.paycr.merchant.service.InventoryService;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	@Autowired
	private InventoryService invnSer;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/new")
	public void newInventory(@RequestBody Inventory inventory, HttpServletResponse response) {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		invnSer.newInventory(inventory, merchant, user.getEmail());
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/update/{inventoryId}")
	public void updateInventory(@RequestBody Inventory inventory, @PathVariable Integer inventoryId,
			HttpServletResponse response) {
		invnSer.updateInventory(inventory, inventoryId);
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/upload", method = RequestMethod.POST)
	public void uploadInventory(@RequestParam("inventory") MultipartFile inventory, HttpServletResponse response)
			throws IOException {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		invnSer.uploadInventory(inventory, merchant, user.getEmail());
	}

	@RequestMapping("/bulk/upload/format")
	public void downloadFormat(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "Code1,Name1,Rate1,HSN/SAC1,Description1\r\nCode2,Name2,Rate2,HSN/SAC2,Description2";
		response.setHeader("Content-Disposition", "attachment; filename=\"bulkInventory.csv\"");
		response.setContentType("application/csv");
		InputStream is = new ByteArrayInputStream(content.getBytes());
		IOUtils.copy(is, response.getOutputStream());
		response.setContentLength(content.getBytes().length);
		response.flushBuffer();
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping(value = "/bulk/uploads/all", method = RequestMethod.GET)
	public List<BulkInventoryUpload> uploadInventory(HttpServletResponse response) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return invnSer.getUploads(merchant);
	}

	@RequestMapping(value = "/bulk/download/{filename:.+}", method = RequestMethod.GET)
	public byte[] downloadFile(@PathVariable String filename, HttpServletResponse response) throws IOException {
		return invnSer.downloadFile(filename);
	}

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/stats/{inventoryId}")
	public InventoryStats updateInventory(@PathVariable Integer inventoryId, HttpServletResponse response) {
		return invnSer.getStats(inventoryId);
	}
}
