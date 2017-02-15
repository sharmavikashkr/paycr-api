package com.payme.invoice.controller;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payme.common.data.domain.Merchant;
import com.payme.common.data.repository.MerchantRepository;
import com.payme.common.util.HmacSignerUtil;
import com.payme.common.util.RandomIdGenerator;

@RestController
@RequestMapping("merchant")
public class MerchantController {

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@RequestMapping("new")
	public void newMerchant() {
		String secretKey = hmacSigner.signWithSecretKey(UUID.randomUUID().toString(),
				String.valueOf(new Date().getTime()));
		String accessKey = secretKey + secretKey.toLowerCase() + secretKey.toUpperCase();
		accessKey = RandomIdGenerator.generateInvoiceCode(accessKey.toCharArray());
		Merchant merchant = new Merchant();
		merchant.setAccessKey(accessKey);
		merchant.setSecretKey(secretKey);
		merchant.setName("Test");
		merRepo.save(merchant);
	}

}
