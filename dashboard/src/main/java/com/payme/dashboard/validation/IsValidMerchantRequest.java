package com.payme.dashboard.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.payme.common.data.domain.Merchant;
import com.payme.common.data.domain.PmUser;
import com.payme.common.data.repository.UserRepository;
import com.payme.common.exception.PaymeException;
import com.payme.common.util.CommonUtil;
import com.payme.common.util.Constants;
import com.payme.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidMerchantRequest implements RequestValidator<Merchant> {

	@Autowired
	private UserRepository userRepo;

	@Override
	public void validate(Merchant merchant) {
		if (CommonUtil.isNull(merchant)) {
			throw new PaymeException(Constants.FAILURE, "Invalid create merchant request");
		}
		if (CommonUtil.isEmpty(merchant.getAdminName()) || CommonUtil.isEmpty(merchant.getName())
				|| CommonUtil.isEmpty(merchant.getEmail()) || CommonUtil.isEmpty(merchant.getMobile())) {
			throw new PaymeException(Constants.FAILURE, "Mandatory params missing");
		}
		PmUser extUser = userRepo.findByEmail(merchant.getEmail());
		if (CommonUtil.isNotNull(extUser)) {
			throw new PaymeException(Constants.FAILURE, "User already exists with this email");
		}
	}

}
