package com.paycr.admin.service;

import java.util.Date;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Promotion;
import com.paycr.common.data.repository.PromotionRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;

@Service
public class PromotionService {

	private static final Logger logger = LoggerFactory.getLogger(PromotionService.class);

	@Autowired
	private PromotionRepository promoRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private NotifyService<Promotion> notifySer;

	public void sendPromotion(Promotion promotion) {
		logger.info("New promotion request : {}", new Gson().toJson(promotion));
		if (CommonUtil.isNull(promotion) || CommonUtil.isEmpty(promotion.getEmail())
				|| CommonUtil.isEmpty(promotion.getPhone()) || CommonUtil.isEmpty(promotion.getName())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Missing mandatory params");
		}
		if (!(CommonUtil.match(promotion.getEmail(), CommonUtil.EMAIL_PATTERN)
				|| CommonUtil.match(promotion.getName(), CommonUtil.NAME_PATTERN))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid values of params");
		}
		if (CommonUtil.isNotNull(promoRepo.findByEmail(promotion.getEmail()))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Promotion with this email already sent");
		}
		PcUser user = secSer.findLoggedInUser();
		promotion.setCreated(new Date());
		promotion.setCreatedBy(user.getEmail());
		promotion.setSent(false);
		promotion.setNotified(0);
		promoRepo.save(promotion);
		notifySer.notify(promotion);
	}

	public void notify(Integer promoId) {
		logger.info("Re-notify promotion for promoId : {}", promoId);
		Promotion promotion = promoRepo.findById(promoId).get();
		if (CommonUtil.isNull(promotion)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid request");
		}
		notifySer.notify(promotion);
	}

}
