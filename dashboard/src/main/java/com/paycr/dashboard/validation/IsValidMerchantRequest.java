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

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static final String MOBILE_PATTERN = "^[7-9]{1}[0-9]{9}$";

	private static final String NAME_PATTERN = "[a-zA-Z ]*";

	@Override
	public void validate(Merchant merchant) {
		if (CommonUtil.isNull(merchant)) {
			throw new PaycrException(Constants.FAILURE, "Invalid create merchant request");
		}
		if (!(match(merchant.getEmail(), EMAIL_PATTERN) || match(merchant.getMobile(), MOBILE_PATTERN)
				|| match(merchant.getName(), NAME_PATTERN) || match(merchant.getAdminName(), NAME_PATTERN))) {
			throw new PaycrException(Constants.FAILURE, "Invalid values of params");
		}
		PcUser extUser = userRepo.findByEmailOrMobile(merchant.getEmail(), merchant.getMobile());
		if (CommonUtil.isNotNull(extUser)) {
			throw new PaycrException(Constants.FAILURE, "User already exists with this email");
		}
	}

	private boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

}
