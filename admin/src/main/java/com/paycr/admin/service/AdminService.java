package com.paycr.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.admin.validation.PricingValidator;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.AdminSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.PaymentSetting;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.PricingMerchant;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.AdminSettingRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.PricingMerchantRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.PricingType;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;

@Service
public class AdminService {

	@Autowired
	private PricingValidator pricingValidator;

	@Autowired
	private PricingRepository pricingRepo;

	@Autowired
	private PricingMerchantRepository priMerRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private AdminSettingRepository adsetRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	public void createPricing(Pricing pricing) {
		pricingValidator.validate(pricing);
		pricingRepo.save(pricing);
	}

	public void togglePricing(Integer pricingId) {
		Pricing pri = pricingRepo.findOne(pricingId);
		if (pri.isActive()) {
			pri.setActive(false);
		} else {
			pri.setActive(true);
		}
		pricingRepo.save(pri);
	}

	public AdminSetting getSetting() {
		return adsetRepo.findAll().get(0);
	}

	public void saveSetting(AdminSetting setting) {
		AdminSetting adset = adsetRepo.findAll().get(0);
		adset.setBanner(setting.getBanner());
		adset.setGstin(setting.getGstin());
		PaymentSetting payset = adset.getPaymentSetting();
		payset.setRzpMerchantId(setting.getPaymentSetting().getRzpMerchantId());
		payset.setRzpKeyId(setting.getPaymentSetting().getRzpKeyId());
		payset.setRzpSecretId(setting.getPaymentSetting().getRzpSecretId());
		adsetRepo.save(adset);
	}

	public void saveAddress(Address newAddr) {
		if (CommonUtil.isNull(newAddr) || CommonUtil.isEmpty(newAddr.getAddressLine1())
				|| CommonUtil.isEmpty(newAddr.getCity()) || CommonUtil.isEmpty(newAddr.getState())
				|| CommonUtil.isEmpty(newAddr.getPincode()) || CommonUtil.isEmpty(newAddr.getCountry())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Address");
		}
		AdminSetting adset = adsetRepo.findAll().get(0);
		Address exstAddr = adset.getAddress();
		if (CommonUtil.isNull(exstAddr)) {
			exstAddr = new Address();
		}
		exstAddr.setAddressLine1(newAddr.getAddressLine1());
		exstAddr.setAddressLine2(newAddr.getAddressLine2());
		exstAddr.setCity(newAddr.getCity());
		exstAddr.setState(newAddr.getState());
		exstAddr.setPincode(newAddr.getPincode());
		exstAddr.setCountry(newAddr.getCountry());
		adset.setAddress(exstAddr);
		adsetRepo.save(adset);
	}

	public void newTaxMaster(TaxMaster tax) {
		if (CommonUtil.isNull(tax) || CommonUtil.isNull(tax.getName()) || CommonUtil.isNull(tax.getValue())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Tax request");
		}
		if (tax.isChild()) {
			if (CommonUtil.isNull(tax.getParentTaxId())) {
				throw new PaycrException(Constants.FAILURE, "Child tax must have a parent");
			}
			TaxMaster parent = taxMRepo.findOne(tax.getParentTaxId());
			if (CommonUtil.isNull(parent) || !parent.isActive()) {
				throw new PaycrException(Constants.FAILURE, "Parent tax not found");
			}
			tax.setTaxParent(parent);
		}
		tax.setActive(true);
		taxMRepo.save(tax);
	}

	public void addPricingMerchant(Integer pricingId, Integer merchantId) {
		try {
			Pricing pricing = pricingRepo.findOne(pricingId);
			Merchant merchant = merRepo.findOne(merchantId);
			if (CommonUtil.isNull(pricing) || CommonUtil.isNull(merchant)
					|| PricingType.PUBLIC.equals(pricing.getType())) {
				throw new PaycrException(Constants.FAILURE, "Invalid Request");
			}
			PricingMerchant priMerExt = priMerRepo.findByMerchantAndPricing(merchant, pricing);
			if (CommonUtil.isNotNull(priMerExt)) {
				throw new PaycrException(Constants.FAILURE, "Pricing already active for merchant");
			}
			priMerExt = new PricingMerchant();
			priMerExt.setMerchant(merchant);
			priMerExt.setPricing(pricing);
			priMerRepo.save(priMerExt);
		} catch (Exception ex) {
			throw new PaycrException(Constants.FAILURE, ex.getMessage());
		}
	}

	public List<Merchant> getMerchantForPricing(Integer pricingId) {
		Pricing pricing = pricingRepo.findOne(pricingId);
		return priMerRepo.findMerchantsForPricing(pricing);
	}

}
