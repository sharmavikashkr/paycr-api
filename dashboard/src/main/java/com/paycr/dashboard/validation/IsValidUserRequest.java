package com.paycr.dashboard.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(0)
public class IsValidUserRequest implements RequestValidator<PcUser> {

	@Autowired
	private UserRepository userRepo;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static final String MOBILE_PATTERN = "^[7-9]{1}[0-9]{9}$";

	private static final String NAME_PATTERN = "[a-zA-Z ]*";

	@Override
	public void validate(PcUser user) {
		if (CommonUtil.isNull(user)) {
			throw new PaycrException(Constants.FAILURE, "Invalid create user request");
		}
		if (!(match(user.getEmail(), EMAIL_PATTERN) || match(user.getMobile(), MOBILE_PATTERN)
				|| match(user.getName(), NAME_PATTERN))) {
			throw new PaycrException(Constants.FAILURE, "Invalid values of params");
		}
		List<PcUser> extUserList = userRepo.findByEmailOrMobile(user.getEmail(), user.getMobile());
		if (CommonUtil.isNotEmpty(extUserList)) {
			throw new PaycrException(Constants.FAILURE, "User already exists with this email/mobile");
		}
	}

	private boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

}
