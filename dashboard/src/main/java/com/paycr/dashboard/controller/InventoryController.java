package com.paycr.dashboard.controller;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.bean.InventoryStats;
import com.paycr.common.data.domain.Inventory;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.InventoryService;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	@Autowired
	private InventoryService invnSer;

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/new")
	public void newInventory(@RequestBody Inventory inventory, HttpServletResponse response) {
		try {
			invnSer.newInventory(inventory);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/update/{inventoryId}")
	public void updateInventory(@RequestBody Inventory inventory, @PathVariable Integer inventoryId,
			HttpServletResponse response) {
		try {
			invnSer.updateInventory(inventory, inventoryId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.MERCHANT_FINANCE_AUTH)
	@RequestMapping("/stats/{inventoryId}")
	public InventoryStats updateInventory(@PathVariable Integer inventoryId, HttpServletResponse response) {
		try {
			return invnSer.getStats(inventoryId);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}
}
