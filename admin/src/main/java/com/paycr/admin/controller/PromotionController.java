package com.paycr.admin.controller;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
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
	@RequestMapping("/send")
	public void sendPromotion(@RequestBody Promotion promotion, HttpServletResponse httpResponse) {
		try {
			promoSer.sendPromotion(promotion);
		} catch (Exception ex) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST_400);
			httpResponse.addHeader("error_message", ex.getMessage());
		}
	}

	@PreAuthorize(RoleUtil.PAYCR_ADMIN_AUTH)
	@RequestMapping("/notify/{promoId}")
	public void notifyPromotion(@PathVariable Integer promoId, HttpServletResponse httpResponse) {
		try {
			promoSer.notify(promoId);
		} catch (Exception ex) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST_400);
			httpResponse.addHeader("error_message", ex.getMessage());
		}
	}

}
