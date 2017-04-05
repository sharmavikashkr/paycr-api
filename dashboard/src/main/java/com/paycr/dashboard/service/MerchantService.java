package com.paycr.dashboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;

@Service
public class MerchantService {

	@Autowired
	private MerchantRepository merRepo;

	public String newCustomParam(Merchant merchant, MerchantCustomParam customParam) {
		try {
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
			return "FAILURE";
		}
	}

	public String deleteCustomParam(Merchant merchant, Integer id) {
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
	}

	public String updateSetting(Merchant merchant, MerchantSetting setting) {
		merchant.getSetting().setSendSms(setting.isSendSms());
		merchant.getSetting().setSendEmail(setting.isSendEmail());
		merchant.getSetting().setExpiryDays(setting.getExpiryDays());
		merchant.getSetting().setRzpMerchantId(setting.getRzpMerchantId());
		merchant.getSetting().setRzpKeyId(setting.getRzpKeyId());
		merchant.getSetting().setRzpSecretId(setting.getRzpSecretId());
		merRepo.save(merchant);
		return "Settings saved";
	}

}
