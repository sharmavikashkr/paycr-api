package com.paycr.admin.service;

import java.util.Date;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Promotion;
import com.paycr.common.data.repository.PromotionRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;

@Service
public class PromotionService {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static final String NAME_PATTERN = "[a-zA-Z ]*";

	@Autowired
	private PromotionRepository promoRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private NotifyService<Promotion> notifySer;

	public void sendPromotion(Promotion promotion) {
		if (CommonUtil.isNull(promotion) || CommonUtil.isEmpty(promotion.getEmail())
				|| CommonUtil.isEmpty(promotion.getPhone()) || CommonUtil.isEmpty(promotion.getName())) {
			throw new PaycrException(HttpStatus.BAD_REQUEST_400, "Missing mandatory params");
		}
		if (!(match(promotion.getEmail(), EMAIL_PATTERN) || match(promotion.getName(), NAME_PATTERN))) {
			throw new PaycrException(Constants.FAILURE, "Invalid values of params");
		}
		if (CommonUtil.isNotNull(promoRepo.findByEmail(promotion.getEmail()))) {
			throw new PaycrException(Constants.FAILURE, "Promotion with this email already sent");
		}
		PcUser user = secSer.findLoggedInUser();
		promotion.setCreated(new Date());
		promotion.setCreatedBy(user.getEmail());
		promotion.setSent(false);
		promotion.setNotified(0);
		promoRepo.save(promotion);
		notifySer.notify(promotion);
	}

	private boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

	public void notify(Integer promoId) {
		Promotion promotion = promoRepo.findOne(promoId);
		if (CommonUtil.isNull(promotion)) {
			throw new PaycrException(Constants.FAILURE, "Invalid request");
		}
		notifySer.notify(promotion);
	}

}
