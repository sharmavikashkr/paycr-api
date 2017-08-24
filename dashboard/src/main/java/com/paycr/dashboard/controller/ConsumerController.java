package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.util.RoleUtil;
import com.paycr.dashboard.service.ConsumerService;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

	@Autowired
	private ConsumerService conSer;

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/get")
	public List<Consumer> getAllConsumer() {
		return conSer.getAllConsumer();
	}
}
