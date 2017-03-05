package com.paycr.dashboard.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidMerchantRequest implements RequestValidator<Merchant> {

	@Autowired
	private UserRepository userRepo;

	@Override
	public void validate(Merchant merchant) {
		if (CommonUtil.isNull(merchant)) {
			throw new PaycrException(Constants.FAILURE, "Invalid create merchant request");
		}
		if (CommonUtil.isEmpty(merchant.getAdminName()) || CommonUtil.isEmpty(merchant.getName())
				|| CommonUtil.isEmpty(merchant.getEmail()) || CommonUtil.isEmpty(merchant.getMobile())) {
			throw new PaycrException(Constants.FAILURE, "Mandatory params missing");
		}
		PcUser extUser = userRepo.findByEmail(merchant.getEmail());
		if (CommonUtil.isNotNull(extUser)) {
			throw new PaycrException(Constants.FAILURE, "User already exists with this email");
		}
	}

}
