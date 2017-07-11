package com.paycr.dashboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.InvoiceSettingRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;

@Service
public class MerchantService {

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private InvoiceSettingRepository invSetRepo;

	@Autowired
	private InvoiceRepository invRepo;

	public void updateAccount(Merchant merchant, Merchant mer) {
		merchant.setName(mer.getName());
		if (mer.getAddress() != null) {
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
		}
		merRepo.save(merchant);
	}

	public void newCustomParam(Integer settingId, MerchantCustomParam customParam) {
		InvoiceSetting invoiceSetting = invSetRepo.findOne(settingId);
		List<MerchantCustomParam> customParams = invoiceSetting.getCustomParams();
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
		customParam.setInvoiceSetting(invoiceSetting);
		invSetRepo.save(invoiceSetting);
	}

	public void deleteCustomParam(Integer settingId, Integer id) {
		InvoiceSetting invoiceSetting = invSetRepo.findOne(settingId);
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
			throw new PaycrException(Constants.FAILURE, "Custom Param does not exists");
		}
		invSetRepo.save(invoiceSetting);
	}

	public void updateInvoiceSetting(Merchant merchant, InvoiceSetting updatedSetting) {
		updatedSetting.setMerchant(merchant);
		for(MerchantCustomParam mcp : updatedSetting.getCustomParams()) {
			mcp.setInvoiceSetting(updatedSetting);
		}
		invSetRepo.save(updatedSetting);
	}

	public void updatePaymentSetting(Merchant merchant, PaymentSetting updatedSetting) {
		PaymentSetting paySet = merchant.getPaymentSetting();
		paySet.setRzpKeyId(updatedSetting.getRzpKeyId());
		paySet.setRzpMerchantId(updatedSetting.getRzpMerchantId());
		paySet.setRzpSecretId(updatedSetting.getRzpSecretId());
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
