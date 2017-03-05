package com.paycr.invoice.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCustomParam;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(3)
public class IsValidInvoiceCustomParams implements RequestValidator<Invoice> {

	@Autowired
	private MerchantRepository merRepo;

	@Override
	public void validate(Invoice invoice) {
		Merchant merchant = merRepo.findOne(invoice.getMerchant());
		List<MerchantCustomParam> merchantCustomParams = merchant.getCustomParams();
		for (MerchantCustomParam mcp : merchantCustomParams) {
			if (ParamValueProvider.MERCHANT.equals(mcp.getProvider())) {
				boolean mandatoryParamMissing = true;
				for (InvoiceCustomParam icp : invoice.getCustomParams()) {
					if (icp.getParamName().equalsIgnoreCase(mcp.getParamName())) {
						mandatoryParamMissing = false;
						icp.setInvoice(invoice);
					}
				}
				if (mandatoryParamMissing) {
					throw new PaycrException(Constants.FAILURE, "Mandatory Custom Params missing");
				}
			}
		}
	}

}
