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
		welcomePri.setDuration(45);
		welcomePri.setName("WELCOME");
		welcomePri.setRate(BigDecimal.ZERO);
		welcomePri.setLimit(1000);
		welcomePri.setType(PricingType.PUBLIC);
		TaxMaster noTax = createNoTaxMaster();
		welcomePri.setInterstateTax(noTax);
		welcomePri.setIntrastateTax(noTax);
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
		TaxMaster gst18 = taxMRepo.findByNameAndValue("GST", 18F);
		if (CommonUtil.isNull(gst18)) {
			gst18 = new TaxMaster();
			gst18.setActive(true);
			gst18.setChild(false);
			gst18.setName("GST");
			gst18.setValue(18F);
			taxMRepo.save(gst18);
		}

		TaxMaster sgst9 = taxMRepo.findByNameAndValue("SGST", 9F);
		if (CommonUtil.isNull(sgst9)) {
			sgst9 = new TaxMaster();
			sgst9.setActive(true);
			sgst9.setChild(false);
			sgst9.setName("SGST");
			sgst9.setValue(9F);
			sgst9.setChild(true);
			sgst9.setTaxParent(gst18);
			taxMRepo.save(sgst9);
		}

		TaxMaster cgst9 = taxMRepo.findByNameAndValue("CGST", 9F);
		if (CommonUtil.isNull(cgst9)) {
			cgst9 = new TaxMaster();
			cgst9.setActive(true);
			cgst9.setChild(false);
			cgst9.setName("CGST");
			cgst9.setValue(9F);
			cgst9.setChild(true);
			cgst9.setTaxParent(gst18);
			taxMRepo.save(cgst9);
		}

		TaxMaster igst18 = taxMRepo.findByNameAndValue("IGST", 18F);
		if (CommonUtil.isNull(igst18)) {
			igst18 = new TaxMaster();
			igst18.setActive(true);
			igst18.setChild(false);
			igst18.setName("IGST");
			igst18.setValue(18F);
			igst18.setChild(false);
			taxMRepo.save(igst18);
		}

		TaxMaster gst12 = taxMRepo.findByNameAndValue("GST", 12F);
		if (CommonUtil.isNull(gst12)) {
			gst12 = new TaxMaster();
			gst12.setActive(true);
			gst12.setChild(false);
			gst12.setName("GST");
			gst12.setValue(12F);
			taxMRepo.save(gst12);
		}

		TaxMaster sgst6 = taxMRepo.findByNameAndValue("SGST", 6F);
		if (CommonUtil.isNull(sgst6)) {
			sgst6 = new TaxMaster();
			sgst6.setActive(true);
			sgst6.setChild(false);
			sgst6.setName("SGST");
			sgst6.setValue(6F);
			sgst6.setChild(true);
			sgst6.setTaxParent(gst12);
			taxMRepo.save(sgst6);
		}

		TaxMaster cgst6 = taxMRepo.findByNameAndValue("CGST", 6F);
		if (CommonUtil.isNull(cgst6)) {
			cgst6 = new TaxMaster();
			cgst6.setActive(true);
			cgst6.setChild(false);
			cgst6.setName("CGST");
			cgst6.setValue(6F);
			cgst6.setChild(true);
			cgst6.setTaxParent(gst12);
			taxMRepo.save(cgst6);
		}

		TaxMaster igst12 = taxMRepo.findByNameAndValue("IGST", 12F);
		if (CommonUtil.isNull(igst12)) {
			igst12 = new TaxMaster();
			igst12.setActive(true);
			igst12.setChild(false);
			igst12.setName("IGST");
			igst12.setValue(12F);
			igst12.setChild(false);
			taxMRepo.save(igst12);
		}

		TaxMaster gst28 = taxMRepo.findByNameAndValue("GST", 28F);
		if (CommonUtil.isNull(gst28)) {
			gst28 = new TaxMaster();
			gst28.setActive(true);
			gst28.setChild(false);
			gst28.setName("GST");
			gst28.setValue(28F);
			taxMRepo.save(gst28);
		}

		TaxMaster sgst14 = taxMRepo.findByNameAndValue("SGST", 14F);
		if (CommonUtil.isNull(sgst14)) {
			sgst14 = new TaxMaster();
			sgst14.setActive(true);
			sgst14.setChild(false);
			sgst14.setName("SGST");
			sgst14.setValue(14F);
			sgst14.setChild(true);
			sgst14.setTaxParent(gst28);
			taxMRepo.save(sgst14);
		}

		TaxMaster cgst14 = taxMRepo.findByNameAndValue("CGST", 14F);
		if (CommonUtil.isNull(cgst14)) {
			cgst14 = new TaxMaster();
			cgst14.setActive(true);
			cgst14.setChild(false);
			cgst14.setName("CGST");
			cgst14.setValue(14F);
			cgst14.setChild(true);
			cgst14.setTaxParent(gst28);
			taxMRepo.save(cgst14);
		}

		TaxMaster igst28 = taxMRepo.findByNameAndValue("IGST", 28F);
		if (CommonUtil.isNull(igst28)) {
			igst28 = new TaxMaster();
			igst28.setActive(true);
			igst28.setChild(false);
			igst28.setName("IGST");
			igst28.setValue(28F);
			igst28.setChild(false);
			taxMRepo.save(igst28);
		}

		TaxMaster gst5 = taxMRepo.findByNameAndValue("GST", 5F);
		if (CommonUtil.isNull(gst5)) {
			gst5 = new TaxMaster();
			gst5.setActive(true);
			gst5.setChild(false);
			gst5.setName("GST");
			gst5.setValue(5F);
			taxMRepo.save(gst5);
		}

		TaxMaster sgst2d5 = taxMRepo.findByNameAndValue("SGST", 2.5F);
		if (CommonUtil.isNull(sgst2d5)) {
			sgst2d5 = new TaxMaster();
			sgst2d5.setActive(true);
			sgst2d5.setChild(false);
			sgst2d5.setName("SGST");
			sgst2d5.setValue(2.5F);
			sgst2d5.setChild(true);
			sgst2d5.setTaxParent(gst5);
			taxMRepo.save(sgst2d5);
		}

		TaxMaster cgst2d5 = taxMRepo.findByNameAndValue("CGST", 2.5F);
		if (CommonUtil.isNull(cgst2d5)) {
			cgst2d5 = new TaxMaster();
			cgst2d5.setActive(true);
			cgst2d5.setChild(false);
			cgst2d5.setName("CGST");
			cgst2d5.setValue(2.5F);
			cgst2d5.setChild(true);
			cgst2d5.setTaxParent(gst5);
			taxMRepo.save(cgst2d5);
		}

		TaxMaster igst5 = taxMRepo.findByNameAndValue("IGST", 5F);
		if (CommonUtil.isNull(igst5)) {
			igst5 = new TaxMaster();
			igst5.setActive(true);
			igst5.setChild(false);
			igst5.setName("IGST");
			igst5.setValue(5F);
			igst5.setChild(false);
			taxMRepo.save(igst5);
		}

		TaxMaster igstExempt = taxMRepo.findByNameAndValue("EXEMPTED IGST", 0F);
		if (CommonUtil.isNull(igstExempt)) {
			igstExempt = new TaxMaster();
			igstExempt.setActive(true);
			igstExempt.setChild(false);
			igstExempt.setName("EXEMPTED IGST");
			igstExempt.setValue(0F);
			igstExempt.setChild(false);
			taxMRepo.save(igstExempt);
		}

		TaxMaster gstExempt = taxMRepo.findByNameAndValue("EXEMPTED GST", 0F);
		if (CommonUtil.isNull(gstExempt)) {
			gstExempt = new TaxMaster();
			gstExempt.setActive(true);
			gstExempt.setChild(false);
			gstExempt.setName("EXEMPTED GST");
			gstExempt.setValue(0F);
			gstExempt.setChild(false);
			taxMRepo.save(gstExempt);
		}

		TaxMaster igstNon = taxMRepo.findByNameAndValue("NON IGST", 0F);
		if (CommonUtil.isNull(igstNon)) {
			igstNon = new TaxMaster();
			igstNon.setActive(true);
			igstNon.setChild(false);
			igstNon.setName("NON IGST");
			igstNon.setValue(0F);
			igstNon.setChild(false);
			taxMRepo.save(igstNon);
		}

		TaxMaster gstNon = taxMRepo.findByNameAndValue("NON GST", 0F);
		if (CommonUtil.isNull(gstNon)) {
			gstNon = new TaxMaster();
			gstNon.setActive(true);
			gstNon.setChild(false);
			gstNon.setName("NON GST");
			gstNon.setValue(0F);
			gstNon.setChild(false);
			taxMRepo.save(gstNon);
		}

		TaxMaster igstNil = taxMRepo.findByNameAndValue("IGST", 0F);
		if (CommonUtil.isNull(igstNil)) {
			igstNil = new TaxMaster();
			igstNil.setActive(true);
			igstNil.setChild(false);
			igstNil.setName("IGST");
			igstNil.setValue(0F);
			igstNil.setChild(false);
			taxMRepo.save(igstNil);
		}

		TaxMaster gstNil = taxMRepo.findByNameAndValue("GST", 0F);
		if (CommonUtil.isNull(gstNil)) {
			gstNil = new TaxMaster();
			gstNil.setActive(true);
			gstNil.setChild(false);
			gstNil.setName("GST");
			gstNil.setValue(0F);
			gstNil.setChild(false);
			taxMRepo.save(gstNil);
		}
	}

}
