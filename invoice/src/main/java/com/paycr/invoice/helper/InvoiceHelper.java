package com.paycr.invoice.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceCustomParam;
import com.paycr.common.data.domain.Item;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.type.InvoiceType;
import com.paycr.invoice.validation.IsValidInvoiceConsumer;
import com.paycr.invoice.validation.IsValidInvoiceMerchantPricing;
import com.paycr.invoice.validation.IsValidInvoiceRequest;

@Component
public class InvoiceHelper {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private MerchantPricingRepository merPriRepo;

	@Autowired
	private IsValidInvoiceRequest isValidRequest;

	@Autowired
	private IsValidInvoiceConsumer isValidConsumer;

	@Autowired
	private IsValidInvoiceMerchantPricing isValidPricing;

	public Invoice prepareChildInvoice(String invoiceCode) {
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		Invoice childInvoice = ObjectUtils.clone(invoice);
		childInvoice.setId(null);
		childInvoice.setInvoiceCode(null);
		childInvoice.setParent(invoice);
		childInvoice.setMerchant(invoice.getMerchant());
		childInvoice.setConsumer(invoice.getConsumer());
		List<Item> newItems = new ArrayList<Item>();
		for (Item item : invoice.getItems()) {
			Item newItem = new Item();
			newItem.setInventory(item.getInventory());
			newItem.setQuantity(item.getQuantity());
			newItem.setPrice(item.getPrice());
			newItem.setInvoice(childInvoice);
			newItems.add(newItem);
		}
		childInvoice.setItems(newItems);
		List<InvoiceCustomParam> params = new ArrayList<InvoiceCustomParam>();
		for (InvoiceCustomParam param : invoice.getCustomParams()) {
			InvoiceCustomParam newParam = new InvoiceCustomParam();
			newParam.setParamName(param.getParamName());
			newParam.setParamValue(param.getParamValue());
			newParam.setProvider(param.getProvider());
			param.setInvoice(childInvoice);
		}
		childInvoice.setAttachments(null);
		childInvoice.setInvoiceNotices(null);
		childInvoice.setCustomParams(params);
		isValidRequest.validate(childInvoice);
		isValidPricing.validate(childInvoice);
		childInvoice.setInvoiceType(InvoiceType.SINGLE);
		childInvoice = invRepo.save(childInvoice);
		MerchantPricing merPri = childInvoice.getMerchantPricing();
		merPri.setInvCount(merPri.getInvCount() + 1);
		merPriRepo.save(merPri);
		return childInvoice;
	}

	public void updateConsumer(Invoice invoice, Consumer consumer) {
		consumer.setActive(true);
		consumer.setCreated(new Date());
		consumer.setMerchant(invoice.getMerchant());
		invoice.setConsumer(consumer);
		isValidConsumer.validate(invoice);
		invRepo.save(invoice);
	}

}
