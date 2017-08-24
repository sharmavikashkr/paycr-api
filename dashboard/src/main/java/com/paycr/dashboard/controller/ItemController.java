package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Item;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.ItemService;

@RestController
@RequestMapping("/item")
public class ItemController {

	@Autowired
	private ItemService itemSer;

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/get")
	public List<Item> getAllItems() {
		return itemSer.getAllItems();
	}
}
