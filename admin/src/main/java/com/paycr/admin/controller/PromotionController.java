package com.paycr.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.admin.service.PromotionService;
import com.paycr.common.data.domain.Promotion;
import com.paycr.common.util.RoleUtil;

@RestController
@RequestMapping("/promotion")
public class PromotionController {

	@Autowired
	private PromotionService promoSer;

	@PreAuthorize(RoleUtil.PAYCR_ADMIN_AUTH)
	@PostMapping("/send")
	public void sendPromotion(@RequestBody Promotion promotion) {
		promoSer.sendPromotion(promotion);
	}

	@PreAuthorize(RoleUtil.PAYCR_ADMIN_AUTH)
	@GetMapping("/notify/{promoId}")
	public void notifyPromotion(@PathVariable Integer promoId) {
		promoSer.notify(promoId);
	}

}
