package com.payme.invoice.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.payme.common.bean.Payme;
import com.payme.common.data.domain.ConsumerDetail;
import com.payme.common.data.domain.Invoice;
import com.payme.common.data.domain.ItemDetail;
import com.payme.common.data.domain.Merchant;
import com.payme.common.data.repository.ConsumerDetailRepository;
import com.payme.common.data.repository.InvoiceRepository;
import com.payme.common.data.repository.ItemDetailRepository;
import com.payme.common.service.SecurityService;
import com.payme.common.util.CommonUtil;
import com.payme.common.util.HmacSignerUtil;
import com.payme.common.util.RandomIdGenerator;

@RestController
@RequestMapping("invoice")
public class InvoiceController {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private ItemDetailRepository itemRepo;

	@Autowired
	private ConsumerDetailRepository consRepo;

	@Autowired
	private Payme payme;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Secured({ "ROLE_MERCHANT" })
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String single(@RequestBody Invoice invoice) {
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		String charset = hmacSigner.signWithSecretKey(merchant.getSecretKey(), String.valueOf(new Date().getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String invoiceCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
		invoice.setInvoiceCode(invoiceCode);
		invoice.setCreated(new Date());
		invoice.setMerchantId(merchant.getId());
		invRepo.save(invoice);

		List<ItemDetail> itemDetails = invoice.getItemDetails();
		if (CommonUtil.isNotNull(itemDetails)) {
			for (ItemDetail item : itemDetails) {
				item.setInvoiceId(invoice.getId());
				item.setMerchantId(merchant.getId());
			}
			itemRepo.save(itemDetails);
		}

		ConsumerDetail consumerDetail = invoice.getConsumerDetail();
		if (CommonUtil.isNotNull(consumerDetail)) {
			consumerDetail.setInvoiceId(invoice.getId());
			consumerDetail.setMerchantId(merchant.getId());
			consRepo.save(consumerDetail);
		}

		return payme.getBaseUrl() + "/" + invoiceCode;
	}

}
