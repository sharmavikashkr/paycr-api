package com.paycr.admin.service;

import java.util.List;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.paycr.admin.validation.PricingValidator;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.PricingMerchant;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.PricingMerchantRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.PricingType;
import com.paycr.common.util.CommonUtil;

@Service
public class AdminService {

	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

	@Autowired
	private PricingValidator pricingValidator;

	@Autowired
	private PricingRepository pricingRepo;

	@Autowired
	private PricingMerchantRepository priMerRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private TaxMasterRepository taxMRepo;

	public void createPricing(Pricing pricing) {
		logger.info("New Pricing : {}", new Gson().toJson(pricing));
		pricingValidator.validate(pricing);
		pricingRepo.save(pricing);
	}

	public void togglePricing(Integer pricingId) {
		logger.info("Toggle Pricing id : {}", pricingId);
		Pricing pri = pricingRepo.findById(pricingId).get();
		pri.setActive(!pri.isActive());
		pri.setActive(true);
		pricingRepo.save(pri);
	}

	public void newTaxMaster(TaxMaster tax) {
		logger.info("New tax master : {}", new Gson().toJson(tax));
		if (CommonUtil.isNull(tax) || CommonUtil.isNull(tax.getName()) || CommonUtil.isNull(tax.getValue())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Tax request");
		}
		if (tax.isChild()) {
			if (CommonUtil.isNull(tax.getParentTaxId())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Child tax must have a parent");
			}
			TaxMaster parent = taxMRepo.findById(tax.getParentTaxId()).get();
			if (CommonUtil.isNull(parent) || !parent.isActive()) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Parent tax not found");
			}
			tax.setParent(parent);
		}
		tax.setActive(true);
		taxMRepo.save(tax);
	}

	public void addPricingMerchant(Integer pricingId, Integer merchantId) {
		logger.info("Add pricing : {} for merchant : {}", pricingId, merchantId);
		Pricing pricing = pricingRepo.findById(pricingId).get();
		Merchant merchant = merRepo.findById(merchantId).get();
		if (CommonUtil.isNull(pricing) || CommonUtil.isNull(merchant) || PricingType.PUBLIC.equals(pricing.getType())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Request");
		}
		PricingMerchant priMerExt = priMerRepo.findByMerchantAndPricing(merchant, pricing);
		if (CommonUtil.isNotNull(priMerExt)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Pricing already active for merchant");
		}
		priMerExt = new PricingMerchant();
		priMerExt.setMerchant(merchant);
		priMerExt.setPricing(pricing);
		priMerRepo.save(priMerExt);
	}

	public List<Merchant> getMerchantForPricing(Integer pricingId) {
		Pricing pricing = pricingRepo.findById(pricingId).get();
		return priMerRepo.findMerchantsForPricing(pricing);
	}

	public void removePricingMerchant(Integer pricingId, Integer merchantId) {
		logger.info("Remove pricing : {} for merchant : {}", pricingId, merchantId);
		Pricing pricing = pricingRepo.findById(pricingId).get();
		Merchant merchant = merRepo.findById(merchantId).get();
		if (CommonUtil.isNull(pricing) || CommonUtil.isNull(merchant) || PricingType.PUBLIC.equals(pricing.getType())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Invalid Request");
		}
		PricingMerchant priMer = priMerRepo.findByMerchantAndPricing(merchant, pricing);
		priMerRepo.deleteById(priMer.getId());

	}

}
