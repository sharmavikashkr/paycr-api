package com.paycr.dashboard.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.paycr.common.bean.Company;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.Consumer;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpenseItem;
import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.data.domain.Inventory;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceItem;
import com.paycr.common.data.domain.InvoicePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.domain.Supplier;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.Currency;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.InvoiceType;
import com.paycr.common.type.ObjectType;
import com.paycr.common.type.PayType;
import com.paycr.common.util.CommonUtil;
import com.paycr.expense.validation.ExpenseValidator;
import com.paycr.invoice.validation.InvoiceValidator;

@Component
public class SubscriptionHelper {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionHelper.class);

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private ExpenseRepository expRepo;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private TimelineService tlService;

	@Autowired
	private ExpenseValidator expVal;

	@Autowired
	private InvoiceValidator invVal;

	@Autowired
	private Company company;

	@Async
	@Transactional
	public void addToExpense(Integer merchantId, Subscription subs) {
		if (company.getMerchantId() == merchantId || subs.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		logger.info("Adding subscription : {} to expense for merchant : {}", subs.getId(), merchantId);
		Merchant merchant = merRepo.findOne(merchantId);
		String createdBy = "SYSTEM";
		Date timeNow = new Date();
		Merchant paycr = merRepo.findOne(company.getMerchantId());
		Expense expense = new Expense();
		expense.setAddItems(true);
		expense.setCreated(timeNow);
		expense.setCreatedBy(createdBy);
		expense.setCurrency(Currency.INR);
		expense.setMerchant(merchant);
		expense.setInvoiceDate(subs.getCreated());
		expense.setInvoiceCode(subs.getSubscriptionCode());
		expense.setPayAmount(subs.getPayAmount());
		expense.setTotal(subs.getTotal());
		expense.setTotalPrice(subs.getPayAmount());
		expense.setShipping(BigDecimal.ZERO);
		expense.setDiscount(BigDecimal.ZERO);
		ExpenseItem item = new ExpenseItem();
		Asset asset = new Asset();
		asset.setCode(subs.getPricing().getCode());
		asset.setName(subs.getPricing().getName());
		asset.setDescription(subs.getPricing().getDescription());
		asset.setHsnsac(subs.getPricing().getHsnsac());
		asset.setRate(subs.getPricing().getRate());
		asset.setTax(subs.getTax());
		item.setAsset(asset);
		item.setPrice(subs.getPayAmount());
		item.setQuantity(subs.getQuantity());
		item.setTax(subs.getTax());
		List<ExpenseItem> itemList = new ArrayList<ExpenseItem>();
		itemList.add(item);
		expense.setItems(itemList);
		Supplier supplier = new Supplier();
		supplier.setName(paycr.getName());
		supplier.setEmail(paycr.getEmail());
		supplier.setMobile(paycr.getMobile());
		supplier.setGstin(paycr.getGstin());
		if (CommonUtil.isNotNull(paycr.getAddress())) {
			Address addr = new Address();
			addr.setAddressLine1(paycr.getAddress().getAddressLine1());
			addr.setAddressLine2(paycr.getAddress().getAddressLine2());
			addr.setCity(paycr.getAddress().getCity());
			addr.setState(paycr.getAddress().getState());
			addr.setPincode(paycr.getAddress().getPincode());
			addr.setCountry(paycr.getAddress().getCountry());
			supplier.setAddress(addr);
		}
		expense.setSupplier(supplier);
		expVal.validate(expense);
		expRepo.save(expense);
		tlService.saveToTimeline(expense.getId(), ObjectType.EXPENSE, "Expense created", true, createdBy);
		ExpensePayment expPay = new ExpensePayment();
		expPay.setCreated(timeNow);
		expPay.setPaidDate(subs.getCreated());
		expPay.setStatus("captured");
		expPay.setAmount(expense.getPayAmount());
		expPay.setPayType(PayType.SALE);
		expPay.setExpenseCode(expense.getExpenseCode());
		expPay.setMethod(subs.getMethod());
		expPay.setPaymentRefNo(subs.getPaymentRefNo());
		expPay.setPayMode(subs.getPayMode());
		expPay.setMerchant(merchant);
		expense.setPayment(expPay);
		expense.setStatus(ExpenseStatus.PAID);
		expRepo.save(expense);
		tlService.saveToTimeline(expense.getId(), ObjectType.EXPENSE, "Expense marked paid", true, createdBy);
	}

	@Async
	@Transactional
	public void addToInvoice(Integer merchantId, Subscription subs) {
		if (company.getMerchantId() == merchantId || subs.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		logger.info("Adding subscription : {} to paycr invoice for merchant : {}", subs.getId(), merchantId);
		Merchant merchant = merRepo.findOne(merchantId);
		Merchant paycr = merRepo.findOne(company.getMerchantId());
		String createdBy = "SYSTEM";
		Date timeNow = new Date();
		Invoice invoice = new Invoice();
		invoice.setAddItems(true);
		invoice.setCreated(timeNow);
		invoice.setCreatedBy(createdBy);
		invoice.setCurrency(Currency.INR);
		invoice.setMerchant(paycr);
		invoice.setInvoiceType(InvoiceType.SINGLE);
		invoice.setInvoiceDate(subs.getCreated());
		invoice.setInvoiceCode(subs.getSubscriptionCode());
		invoice.setPayAmount(subs.getPayAmount());
		invoice.setTotal(subs.getTotal());
		invoice.setTotalPrice(subs.getPayAmount());
		invoice.setShipping(BigDecimal.ZERO);
		invoice.setDiscount(BigDecimal.ZERO);
		invoice.setExpiresIn(paycr.getInvoiceSetting().getExpiryDays());
		InvoiceItem item = new InvoiceItem();
		Inventory invn = new Inventory();
		invn.setCode(subs.getPricing().getCode());
		invn.setName(subs.getPricing().getName());
		invn.setDescription(subs.getPricing().getDescription());
		invn.setHsnsac(subs.getPricing().getHsnsac());
		invn.setRate(subs.getPricing().getRate());
		invn.setTax(subs.getTax());
		item.setInventory(invn);
		item.setPrice(subs.getPayAmount());
		item.setQuantity(subs.getQuantity());
		item.setTax(subs.getTax());
		List<InvoiceItem> itemList = new ArrayList<InvoiceItem>();
		itemList.add(item);
		invoice.setItems(itemList);
		Consumer consumer = new Consumer();
		consumer.setName(merchant.getName());
		consumer.setEmail(merchant.getEmail());
		consumer.setMobile(merchant.getMobile());
		consumer.setGstin(merchant.getGstin());
		if (CommonUtil.isNotNull(merchant.getAddress())) {
			Address addr = new Address();
			addr.setAddressLine1(merchant.getAddress().getAddressLine1());
			addr.setAddressLine2(merchant.getAddress().getAddressLine2());
			addr.setCity(merchant.getAddress().getCity());
			addr.setState(merchant.getAddress().getState());
			addr.setPincode(merchant.getAddress().getPincode());
			addr.setCountry(merchant.getAddress().getCountry());
			consumer.setBillingAddress(addr);
		}
		invoice.setConsumer(consumer);
		invVal.validate(invoice);
		invRepo.save(invoice);
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Invoice created", true, createdBy);
		InvoicePayment invPay = new InvoicePayment();
		invPay.setCreated(timeNow);
		invPay.setPaidDate(subs.getCreated());
		invPay.setStatus("captured");
		invPay.setAmount(invoice.getPayAmount());
		invPay.setPayType(PayType.SALE);
		invPay.setInvoiceCode(invoice.getInvoiceCode());
		invPay.setMethod(subs.getMethod());
		invPay.setPaymentRefNo(subs.getPaymentRefNo());
		invPay.setPayMode(subs.getPayMode());
		invPay.setMerchant(paycr);
		invoice.setPayment(invPay);
		invoice.setStatus(InvoiceStatus.PAID);
		invRepo.save(invoice);
		tlService.saveToTimeline(invoice.getId(), ObjectType.INVOICE, "Invoice marked paid", true, createdBy);
	}

}
