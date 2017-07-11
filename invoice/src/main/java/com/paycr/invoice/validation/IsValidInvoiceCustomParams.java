package com.paycr.invoice.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCustomParam;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(3)
public class IsValidInvoiceCustomParams implements RequestValidator<Invoice> {

	@Autowired
	private SecurityService secSer;

	@Override
	public void validate(Invoice invoice) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		List<MerchantCustomParam> merchantCustomParams = merchant.getInvoiceSetting().getCustomParams();
		for (InvoiceCustomParam icp : invoice.getCustomParams()) {
			boolean paramFound = false;
			for (MerchantCustomParam mcp : merchantCustomParams) {
				if (icp.getParamName().equals(mcp.getParamName())) {
					paramFound = true;
					if (!icp.getProvider().equals(mcp.getProvider())) {
						throw new PaycrException(Constants.FAILURE, "Custom Params mismatch");
					}
				}
			}
			if (!paramFound) {
				throw new PaycrException(Constants.FAILURE, "Custom Params " + icp.getParamName() + " not configured");
			}
			if (CommonUtil.isNull(icp.getParamValue())) {
				icp.setParamValue("");
			}
			icp.setInvoice(invoice);
		}
	}

}
