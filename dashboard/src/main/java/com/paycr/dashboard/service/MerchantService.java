package com.paycr.dashboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;

@Service
public class MerchantService {

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private InvoiceRepository invRepo;

	public void updateAccount(Merchant merchant, Merchant mer) {
		merchant.setName(mer.getName());
		Address address = merchant.getAddress();
		if (address == null) {
			address = new Address();
		}
		address.setAddressLine1(mer.getAddress().getAddressLine1());
		address.setAddressLine2(mer.getAddress().getAddressLine2());
		address.setCity(mer.getAddress().getCity());
		address.setDistrict(mer.getAddress().getDistrict());
		address.setState(mer.getAddress().getState());
		address.setCountry(mer.getAddress().getCountry());
		address.setPincode(mer.getAddress().getPincode());
		merchant.setAddress(address);
		merRepo.save(merchant);
	}

	public void newCustomParam(Merchant merchant, MerchantCustomParam customParam) {
		List<MerchantCustomParam> customParams = merchant.getSetting().getCustomParams();
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
		customParam.setMerchantSetting(merchant.getSetting());
		merRepo.save(merchant);
	}

	public JsonObject getSetting(MerchantSetting setting) {
		try {
			JsonObject json = new JsonObject();
			json.addProperty("sendEmail", setting.isSendEmail());
			json.addProperty("sendSms", setting.isSendSms());
			json.addProperty("expiryDays", setting.getExpiryDays());
			json.addProperty("rzpMerchantId", setting.getRzpMerchantId());
			json.addProperty("rzpKeyId", setting.getRzpKeyId());
			json.addProperty("rzpSecretId", setting.getRzpSecretId());
			List<MerchantCustomParam> customParams = setting.getCustomParams();
			JsonArray jsonArr = new JsonArray();
			for (MerchantCustomParam cp : customParams) {
				JsonObject cpJson = new JsonObject();
				cpJson.addProperty("id", cp.getId());
				cpJson.addProperty("paramName", cp.getParamName());
				cpJson.addProperty("provider", cp.getProvider().name());
				jsonArr.add(cpJson);
			}
			json.add("customParams", jsonArr);
			return json;
		} catch (Exception ex) {
			throw new PaycrException(Constants.FAILURE, "Bad Request");
		}
	}

	public void deleteCustomParam(Merchant merchant, Integer id) {
		List<MerchantCustomParam> customParams = merchant.getSetting().getCustomParams();
		boolean found = false;
		for (MerchantCustomParam param : customParams) {
			if (param.getId() == id) {
				customParams.remove(param);
				param.setMerchantSetting(null);
				found = true;
				break;
			}
		}
		if (!found) {
			throw new PaycrException(Constants.FAILURE, "Custom Param does not exists");
		}
		merRepo.save(merchant);
	}

	public void updateSetting(Merchant merchant, MerchantSetting setting) {
		merchant.getSetting().setSendSms(setting.isSendSms());
		merchant.getSetting().setSendEmail(setting.isSendEmail());
		merchant.getSetting().setExpiryDays(setting.getExpiryDays());
		merchant.getSetting().setRzpMerchantId(setting.getRzpMerchantId());
		merchant.getSetting().setRzpKeyId(setting.getRzpKeyId());
		merchant.getSetting().setRzpSecretId(setting.getRzpSecretId());
		merRepo.save(merchant);
	}

	public List<Invoice> myInvoices(PcUser user) {
		List<Invoice> myInvoices = invRepo.findInvoicesForMerchant(user.getEmail(), user.getMobile());
		for (Invoice invoice : myInvoices) {
			Merchant invMer = merRepo.findOne(invoice.getMerchant());
			invoice.setMerchantName(invMer.getName());
		}
		return myInvoices;
	}

}
