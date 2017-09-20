package com.paycr.dashboard.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Inventory;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.InventoryService;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	@Autowired
	private InventoryService invnSer;

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/get")
	public List<Inventory> getMyInventory(HttpServletResponse response) {
		try {
			return invnSer.getMyInventory();
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			response.addHeader("error_message", ex.getMessage());
		}
		return null;
	}

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
}
