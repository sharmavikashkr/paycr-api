package com.paycr.dashboard.validation;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidMerchantRequest implements RequestValidator<Merchant> {

	@Autowired
	private UserRepository userRepo;

	@Override
	public void validate(Merchant merchant) {
		if (CommonUtil.isNull(merchant)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid create merchant request");
		}
		if (!(CommonUtil.match(merchant.getEmail(), CommonUtil.EMAIL_PATTERN)
				|| CommonUtil.match(merchant.getMobile(), CommonUtil.MOBILE_PATTERN)
				|| CommonUtil.match(merchant.getName(), CommonUtil.NAME_PATTERN))) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid values of params");
		}
		List<PcUser> extUserList = userRepo.findByEmailOrMobile(merchant.getEmail(), merchant.getMobile());
		if (CommonUtil.isNotEmpty(extUserList)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "User already exists with this email/mobile");
		}
	}

}
