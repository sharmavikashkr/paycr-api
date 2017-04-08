package com.paycr.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.paycr.common.bean.PaycrResponse;
import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.dashboard.service.SearchService;

@RestController
@RequestMapping("/app/search")
public class AppSearchController {

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private HmacSignerUtil hmacUtil;

	@Autowired
	private SearchService serSer;

	@RequestMapping("/invoice")
	public PaycrResponse searchInvoices(@RequestBody SearchInvoiceRequest request,
			@RequestHeader("accessKey") String accessKey, @RequestHeader("signature") String signature) {
		PaycrResponse resp = new PaycrResponse();
		try {
			Merchant merchant = merRepo.findByAccessKey(accessKey);
			String data = request.getInvoiceCode() + request.getAmount().toString();
			if (!signature.equals(hmacUtil.signWithSecretKey(merchant.getSecretKey(), data))) {
				throw new PaycrException(Constants.FAILURE, "Signature mismatch");
			}
			request.setMerchant(merchant.getId());
			resp.setRespCode(0);
			resp.setRespMsg("SUCCESS");
			resp.setData(new Gson().toJson(serSer.fetchInvoiceList(request)));
			return resp;
		} catch (Exception ex) {
			resp.setRespCode(1);
			resp.setRespMsg("FAILURE");
			if (ex instanceof PaycrException) {
				resp.setData(ex.getMessage());
			} else {
				resp.setData("Invalid Merchant");
			}
			return resp;
		}
	}

}
