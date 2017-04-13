package com.paycr.dashboard.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paycr.common.bean.PaycrResponse;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.dashboard.service.MerchantService;

@RestController
@RequestMapping("/app/merchant")
public class AppMerchantController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private MerchantService merSer;

	@Autowired
	private MerchantUserRepository merUserRepo;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@Autowired
	private HmacSignerUtil hmacUtil;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public PaycrResponse appLogin(@RequestParam(value = "username", required = true) String email,
			@RequestParam(value = "password", required = true) String password,
			@RequestHeader(value = "accessKey", required = true) String accessKey) {
		PaycrResponse resp = new PaycrResponse();
		try {
			PcUser user = userRepo.findByEmail(email);
			if (!bcPassEncode.matches(password, user.getPassword())) {
				throw new PaycrException(Constants.FAILURE, "");
			}
			MerchantUser merUser = merUserRepo.findByUserId(user.getId());
			Merchant merchant = merRepo.findOne(merUser.getMerchantId());
			if (!accessKey.equals(merchant.getAccessKey())) {
				throw new PaycrException(Constants.FAILURE, "");
			}
			resp.setRespCode(0);
			resp.setRespMsg("SUCCESS");
			JsonObject json = new JsonObject();
			json.addProperty("secret_key", merchant.getSecretKey());
			json.addProperty("name", merchant.getName());
			json.addProperty("mobile", merchant.getMobile());
			resp.setData(json.toString());
			return resp;
		} catch (Exception ex) {
			resp.setRespCode(1);
			resp.setRespMsg("We do not recognize you");
			return resp;
		}
	}

	@RequestMapping("/setting")
	public PaycrResponse getSetting(@RequestHeader(value = "accessKey", required = true) String accessKey,
			@RequestHeader(value = "signature", required = true) String signature) {
		PaycrResponse resp = new PaycrResponse();
		try {
			Merchant merchant = merRepo.findByAccessKey(accessKey);
			String data = accessKey;
			if (!signature.equals(hmacUtil.signWithSecretKey(merchant.getSecretKey(), data))) {
				throw new PaycrException(Constants.FAILURE, "Signature mismatch");
			}
			resp.setRespCode(0);
			resp.setRespMsg("SUCCESS");
			resp.setData(new Gson().toJson(merSer.getSetting(merchant.getSetting())));
		} catch (Exception ex) {
			resp.setRespCode(1);
			resp.setRespMsg("FAILURE");
			if (ex instanceof PaycrException) {
				resp.setData(ex.getMessage());
			} else {
				resp.setData("Invalid Merchant");
			}
		}
		return resp;
	}

	@RequestMapping("/setting/update")
	public String updateSetting(@RequestBody MerchantSetting setting,
			@RequestHeader(value = "accessKey", required = true) String accessKey,
			@RequestHeader(value = "signature", required = true) String signature) {
		try {
			Merchant merchant = merRepo.findByAccessKey(accessKey);
			String data = String.valueOf(setting.getExpiryDays());
			if (!signature.equals(hmacUtil.signWithSecretKey(merchant.getSecretKey(), data))) {
				throw new PaycrException(Constants.FAILURE, "Signature mismatch");
			}
			return merSer.updateSetting(merchant, setting);
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}

	@RequestMapping("/customParam/new")
	public String newCustomParam(@Valid @RequestBody MerchantCustomParam customParam,
			@RequestHeader(value = "accessKey", required = true) String accessKey,
			@RequestHeader(value = "signature", required = true) String signature) {
		try {
			Merchant merchant = merRepo.findByAccessKey(accessKey);
			String data = customParam.getParamName();
			if (!signature.equals(hmacUtil.signWithSecretKey(merchant.getSecretKey(), data))) {
				throw new PaycrException(Constants.FAILURE, "Signature mismatch");
			}
			return merSer.newCustomParam(merchant, customParam);
		} catch (Exception ex) {
			return "FAILURE";
		}
	}

	@RequestMapping("/customParam/delete/{id}")
	public String deleteCustomParam(@PathVariable Integer id,
			@RequestHeader(value = "accessKey", required = true) String accessKey,
			@RequestHeader(value = "signature", required = true) String signature) {
		try {
			Merchant merchant = merRepo.findByAccessKey(accessKey);
			String data = id.toString();
			if (!signature.equals(hmacUtil.signWithSecretKey(merchant.getSecretKey(), data))) {
				throw new PaycrException(Constants.FAILURE, "Signature mismatch");
			}
			return merSer.deleteCustomParam(merchant, id);
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}
}
