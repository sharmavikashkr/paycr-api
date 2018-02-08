package com.paycr.invoice.validation;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCustomParam;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(4)
public class IsValidInvoiceCustomParams implements RequestValidator<Invoice> {

	@Override
	public void validate(Invoice invoice) {
		List<MerchantCustomParam> merchantCustomParams = invoice.getMerchant().getInvoiceSetting().getCustomParams();
		for (InvoiceCustomParam icp : invoice.getCustomParams()) {
			if(!icp.isInclude()) {
				continue;
			}
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
			if (!invoice.isUpdate()) {
				icp.setId(null);
			}
		}
	}

}
