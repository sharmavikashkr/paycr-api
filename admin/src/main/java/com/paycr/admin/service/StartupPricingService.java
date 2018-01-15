package com.paycr.admin.service;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.TaxMaster;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.TaxMasterRepository;
import com.paycr.common.type.PricingType;
import com.paycr.common.util.CommonUtil;

@Component
public class StartupPricingService {

	@Autowired
	private TaxMasterRepository taxMRepo;

	@Autowired
	private PricingRepository priRepo;

	public void createWelcomePricing() {
		Pricing welcomePri = priRepo.findByCodeAndActive("PPC-P-001", true);
		if (CommonUtil.isNotNull(welcomePri)) {
			return;
		}
		welcomePri = new Pricing();
		welcomePri.setCode("PPC-P-001");
		welcomePri.setActive(true);
		welcomePri.setCreated(new Date());
		welcomePri.setDescription("Welcome Plan");
		welcomePri.setDuration(90);
		welcomePri.setName("WELCOME");
		welcomePri.setRate(new BigDecimal(0));
		welcomePri.setLimit(1000);
		welcomePri.setType(PricingType.PUBLIC);
		TaxMaster noTax = createNoTaxMaster();
		welcomePri.setTax(noTax);
		priRepo.save(welcomePri);
	}

	private TaxMaster createNoTaxMaster() {
		TaxMaster noTax = taxMRepo.findByName("NO_TAX");
		if (CommonUtil.isNotNull(noTax)) {
			return noTax;
		}
		noTax = new TaxMaster();
		noTax.setActive(true);
		noTax.setChild(false);
		noTax.setName("NO_TAX");
		noTax.setValue(0F);
		return taxMRepo.save(noTax);
	}

	public void createGstTaxMaster() {
		TaxMaster gst = taxMRepo.findByName("GST");
		if (CommonUtil.isNotNull(gst)) {
			return;
		}
		gst = new TaxMaster();
		gst.setActive(true);
		gst.setChild(false);
		gst.setName("GST");
		gst.setValue(18F);
		taxMRepo.save(gst);

		TaxMaster sgst = taxMRepo.findByName("SGST");
		if (CommonUtil.isNotNull(sgst)) {
			return;
		}
		sgst = new TaxMaster();
		sgst.setActive(true);
		sgst.setChild(false);
		sgst.setName("SGST");
		sgst.setValue(9F);
		sgst.setChild(true);
		sgst.setTaxParent(gst);
		taxMRepo.save(sgst);

		TaxMaster cgst = taxMRepo.findByName("CGST");
		if (CommonUtil.isNotNull(cgst)) {
			return;
		}
		cgst = new TaxMaster();
		cgst.setActive(true);
		cgst.setChild(false);
		cgst.setName("CGST");
		cgst.setValue(9F);
		cgst.setChild(true);
		cgst.setTaxParent(gst);
		taxMRepo.save(cgst);
	}

}
