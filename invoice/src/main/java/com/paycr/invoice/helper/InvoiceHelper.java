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
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.ConsumerType;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ObjectType;
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

	@Autowired
	private TimelineService tlService;

	public Invoice prepareChildInvoice(String invoiceCode, InvoiceType invoiceType, String createdBy) {
		Date timeNow = new Date();
		Invoice invoice = invRepo.findByInvoiceCode(invoiceCode);
		Invoice childInvoice = ObjectUtils.clone(invoice);
		childInvoice.setId(null);
		childInvoice.setInvoiceCode(null);
		childInvoice.setParent(invoice);
		childInvoice.setCreated(timeNow);
		childInvoice.setCreatedBy(createdBy);
		childInvoice.setMerchant(invoice.getMerchant());
		childInvoice.setConsumer(invoice.getConsumer());
		List<InvoiceItem> newItems = new ArrayList<>();
		for (InvoiceItem item : invoice.getItems()) {
			InvoiceItem newItem = new InvoiceItem();
			newItem.setInventory(item.getInventory());
			newItem.setQuantity(item.getQuantity());
			newItem.setPrice(item.getPrice());
			newItem.setTax(item.getTax());
			newItem.setInvoice(childInvoice);
			newItems.add(newItem);
		}
		childInvoice.setItems(newItems);
		childInvoice.setAttachments(null);
		childInvoice.setNotices(null);
		isValidRequest.validate(childInvoice);
		isValidPricing.validate(childInvoice);
		List<InvoiceCustomParam> params = new ArrayList<InvoiceCustomParam>();
		for (InvoiceCustomParam param : invoice.getCustomParams()) {
			InvoiceCustomParam newParam = new InvoiceCustomParam();
			newParam.setParamName(param.getParamName());
			newParam.setParamValue(param.getParamValue());
			newParam.setProvider(param.getProvider());
			param.setInvoice(childInvoice);
		}
		childInvoice.setCustomParams(params);
		childInvoice.setInvoiceType(invoiceType);
		childInvoice = invRepo.save(childInvoice);
		MerchantPricing merPri = childInvoice.getMerchantPricing();
		merPri.setUseCount(merPri.getUseCount() + 1);
		merPriRepo.save(merPri);
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE,
				"Child invoice created : " + childInvoice.getInvoiceCode(), true, createdBy);
		tlService.saveToTimeline(childInvoice.getId(), ObjectType.INVOICE, "Invoice created", true, createdBy);
		return childInvoice;
	}

	public void updateConsumer(Invoice invoice, Consumer consumer) {
		consumer.setActive(true);
		consumer.setCreated(new Date());
		consumer.setType(ConsumerType.CUSTOMER);
		invoice.setConsumer(consumer);
		isValidConsumer.validate(invoice);
		invRepo.save(invoice);
	}

}
