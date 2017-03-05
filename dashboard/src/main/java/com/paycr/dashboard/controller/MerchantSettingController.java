package com.paycr.dashboard.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;

@RestController
@RequestMapping("/merchant/setting")
public class MerchantSettingController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private MerchantRepository merRepo;

	@Secured({ "ROLE_MERCHANT" })
	@RequestMapping("/customParam/new")
	public String newCustomParam(@RequestBody MerchantCustomParam customParam, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			List<MerchantCustomParam> customParams = merchant.getCustomParams();
			if (CommonUtil.isNull(customParam) || CommonUtil.isEmpty(customParam.getParamName())
					|| CommonUtil.isNull(customParam.getProvider())) {
				throw new PaycrException(Constants.FAILURE, "Invalid Custom Param");
			}
			if (customParams.size() >= 5) {
				throw new PaycrException(Constants.FAILURE, "Cannot configure more than 5 custom params");
			}
			for (MerchantCustomParam param : customParams) {
				if (param.getParamName().equalsIgnoreCase(customParam.getParamName())) {
					throw new PaycrException(Constants.FAILURE, "Custom Param already exists");
				}
			}
			customParams.add(customParam);
			customParam.setMerchant(merchant);
			merRepo.save(merchant);
			return "Custom Param added";
		} catch (Exception ex) {
			response.setStatus(500);
			return ex.getMessage();
		}
	}

	@Secured({ "ROLE_MERCHANT" })
	@RequestMapping("/customParam/delete/{id}")
	public String deleteCustomParam(@PathVariable Integer id, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			List<MerchantCustomParam> customParams = merchant.getCustomParams();
			boolean found = false;
			for (MerchantCustomParam param : customParams) {
				if (param.getId() == id) {
					customParams.remove(param);
					param.setMerchant(null);
					found = true;
					break;
				}
			}
			if (!found) {
				throw new PaycrException(Constants.FAILURE, "Custom Param does not exists");
			}
			merRepo.save(merchant);
			return "Custom Param deleted";
		} catch (Exception ex) {
			response.setStatus(500);
			return ex.getMessage();
		}
	}

	@Secured({ "ROLE_MERCHANT" })
	@RequestMapping("/update")
	public String resetSendSms(@RequestBody MerchantSetting setting, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			merchant.getSetting().setSendSms(setting.isSendSms());
			merchant.getSetting().setSendEmail(setting.isSendEmail());
			merRepo.save(merchant);
			return "Custom Param deleted";
		} catch (Exception ex) {
			response.setStatus(500);
			return ex.getMessage();
		}
	}

}
