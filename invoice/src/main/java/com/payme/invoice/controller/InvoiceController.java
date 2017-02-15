package com.payme.invoice.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.payme.common.bean.Payme;
import com.payme.common.data.domain.Invoice;
import com.payme.common.data.domain.Merchant;
import com.payme.common.data.repository.InvoiceRepository;
import com.payme.common.data.repository.MerchantRepository;
import com.payme.common.util.HmacSignerUtil;
import com.payme.common.util.RandomIdGenerator;

@RestController
@RequestMapping("invoice")
public class InvoiceController {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private Payme payme;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String single(@RequestBody Invoice invoice) {
		Merchant merchant = merRepo.findByAccessKey(invoice.getMerchant());
		String charset = hmacSigner.signWithSecretKey(merchant.getSecretKey(), String.valueOf(new Date().getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String invoiceCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
		invoice.setInvoiceCode(invoiceCode);
		invoice.setCreated(new Date());
		invRepo.save(invoice);
		return payme.getBaseUrl() + "/" + invoiceCode;
	}

}
