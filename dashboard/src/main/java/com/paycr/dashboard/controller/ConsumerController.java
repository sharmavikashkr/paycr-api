package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.RoleUtil;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private SecurityService secSer;

	@PreAuthorize(RoleUtil.MERCHANT_AUTH)
	@RequestMapping("/get")
	public List<Consumer> getAllConsumer() {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		return invRepo.findConsumersForMerchant(merchant);
	}
}
