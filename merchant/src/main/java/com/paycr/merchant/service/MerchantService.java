package com.paycr.merchant.service;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.GstSetting;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.dashboard.validation.IsValidGstinRequest;

@Service
public class MerchantService {

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private IsValidGstinRequest gstinValid;

	public void updateAccount(Merchant merchant, Merchant mer) {
		merchant.setName(mer.getName());
		merchant.setGstin(mer.getGstin());
		gstinValid.validate(mer.getGstin());
		merRepo.save(merchant);
	}

	public void updateAddress(Merchant merchant, Address addr) {
		if (CommonUtil.isNull(addr) || CommonUtil.isEmpty(addr.getAddressLine1()) || CommonUtil.isEmpty(addr.getCity())
				|| CommonUtil.isEmpty(addr.getState()) || CommonUtil.isEmpty(addr.getPincode())
				|| CommonUtil.isEmpty(addr.getCountry())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Address");
		}
		if (CommonUtil.isNotNull(addr)) {
			Address address = merchant.getAddress();
			if (CommonUtil.isNull(address)) {
				address = new Address();
			}
			address.setAddressLine1(addr.getAddressLine1());
			address.setAddressLine2(addr.getAddressLine2());
			address.setCity(addr.getCity());
			address.setState(addr.getState());
			address.setCountry(addr.getCountry());
			address.setPincode(addr.getPincode());
			merchant.setAddress(address);
		}
		merRepo.save(merchant);
	}

	public void newCustomParam(Merchant merchant, MerchantCustomParam customParam) {
		InvoiceSetting invoiceSetting = merchant.getInvoiceSetting();
		List<MerchantCustomParam> customParams = invoiceSetting.getCustomParams();
		if (CommonUtil.isNull(customParam) || CommonUtil.isEmpty(customParam.getParamName())
				|| CommonUtil.isNull(customParam.getProvider())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Custom Param");
		}
		if (customParams.size() >= 10) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Cannot configure more than 10 custom params");
		}
		for (MerchantCustomParam param : customParams) {
			if (param.getParamName().equalsIgnoreCase(customParam.getParamName())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Custom Param already exists");
			}
		}
		customParams.add(customParam);
		customParam.setInvoiceSetting(invoiceSetting);
		merRepo.save(merchant);
	}

	public void deleteCustomParam(Merchant merchant, Integer id) {
		InvoiceSetting invoiceSetting = merchant.getInvoiceSetting();
		List<MerchantCustomParam> customParams = invoiceSetting.getCustomParams();
		boolean found = false;
		for (MerchantCustomParam param : customParams) {
			if (param.getId() == id) {
				customParams.remove(param);
				param.setInvoiceSetting(null);
				found = true;
				break;
			}
		}
		if (!found) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Custom Param does not exists");
		}
		merRepo.save(merchant);
	}

	public void updateInvoiceSetting(Merchant merchant, InvoiceSetting updatedSetting) {
		updatedSetting.setId(merchant.getInvoiceSetting().getId());
		merchant.setInvoiceSetting(updatedSetting);
		for (MerchantCustomParam mcp : updatedSetting.getCustomParams()) {
			mcp.setInvoiceSetting(updatedSetting);
		}
		merRepo.save(merchant);
	}

	public void updatePaymentSetting(Merchant merchant, PaymentSetting updatedSetting) {
		PaymentSetting paySet = merchant.getPaymentSetting();
		paySet.setRzpKeyId(updatedSetting.getRzpKeyId());
		paySet.setRzpMerchantId(updatedSetting.getRzpMerchantId());
		paySet.setRzpSecretId(updatedSetting.getRzpSecretId());
		merRepo.save(merchant);
	}

	public void updateGstSetting(Merchant merchant, GstSetting newSet) {
		GstSetting gstSet = merchant.getGstSetting();
		gstSet.setFilingPeriod(newSet.getFilingPeriod());
		gstSet.setExpPaid(newSet.isExpPaid());
		gstSet.setExpUnpaid(newSet.isExpUnpaid());
		gstSet.setInvCreated(newSet.isInvCreated());
		gstSet.setInvDeclined(newSet.isInvDeclined());
		gstSet.setInvExpired(newSet.isInvExpired());
		gstSet.setInvPaid(newSet.isInvPaid());
		gstSet.setInvUnpaid(newSet.isInvUnpaid());
		merRepo.save(merchant);
	}

}
