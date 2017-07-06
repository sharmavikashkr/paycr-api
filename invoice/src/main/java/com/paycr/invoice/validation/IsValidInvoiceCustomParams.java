package com.paycr.invoice.validation;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCustomParam;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.validation.RequestValidator;

@Component
@Order(3)
public class IsValidInvoiceCustomParams implements RequestValidator<Invoice> {

	@Override
	public void validate(Invoice invoice) {
		List<MerchantCustomParam> merchantCustomParams = invoice.getInvoiceSetting().getCustomParams();
		for (MerchantCustomParam mcp : merchantCustomParams) {
			boolean mandatoryParamMissing = true;
			for (InvoiceCustomParam icp : invoice.getCustomParams()) {
				if (ParamValueProvider.MERCHANT.equals(mcp.getProvider())
						&& icp.getParamName().equalsIgnoreCase(mcp.getParamName())) {
					if (icp.getParamValue() != null) {
						mandatoryParamMissing = false;
					}
				} else {
					mandatoryParamMissing = false;
				}
				if (CommonUtil.isNull(icp.getParamValue())) {
					icp.setParamValue("");
				}
				icp.setInvoice(invoice);
			}
			if (mandatoryParamMissing) {
				throw new PaycrException(Constants.FAILURE, "Mandatory Custom Params missing");
			}
		}
	}

}
