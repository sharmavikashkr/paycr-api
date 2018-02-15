package com.paycr.dashboard.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.paycr.common.bean.Company;
import com.paycr.common.bean.OfflineSubscription;
import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.Address;
import com.paycr.common.data.domain.AdminSetting;
import com.paycr.common.data.domain.Asset;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpenseItem;
import com.paycr.common.data.domain.ExpensePayment;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.domain.Supplier;
import com.paycr.common.data.repository.AdminSettingRepository;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.SubscriptionRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.TimelineService;
import com.paycr.common.type.Currency;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.type.ObjectType;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PayType;
import com.paycr.common.type.PricingStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.PdfUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.paycr.expense.validation.ExpenseValidator;
import com.razorpay.RazorpayClient;

@Service
public class SubscriptionService {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private MerchantPricingRepository merPriRepo;

	@Autowired
	private PricingRepository priRepo;

	@Autowired
	private ExpenseRepository expRepo;

	@Autowired
	private TimelineService tlService;

	@Autowired
	private ExpenseValidator expVal;

	@Autowired
	private SubscriptionRepository subsRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private AdminSettingRepository adsetRepo;

	@Autowired
	private Server server;

	@Autowired
	private Company company;

	@Autowired
	private PdfUtil pdfUtil;

	public Subscription getSubscriptionByCode(String subscriptionCode) {
		Subscription subs = subsRepo.findBySubscriptionCode(subscriptionCode);
		if (CommonUtil.isNull(subs)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Subscription not found");
		}
		return subs;
	}

	public Subscription getSubscription(Integer pricingId) {
		MerchantPricing merPricing = merPriRepo.findOne(pricingId);
		return merPricing.getSubscription();
	}

	public void offlineSubscription(OfflineSubscription offline) {
		logger.info("Offline subscription request : {}", new Gson().toJson(offline));
		Date timeNow = new Date();
		Merchant merchant = merRepo.findOne(offline.getMerchantId());
		Pricing pricing = priRepo.findOne(offline.getPricingId());
		AdminSetting adset = adsetRepo.findAll().get(0);
		Subscription subs = new Subscription();
		if (CommonUtil.isNull(merchant.getAddress()) || CommonUtil.isEmpty(merchant.getAddress().getState())) {
			if (!"NO_TAX".equalsIgnoreCase(pricing.getInterstateTax().getName())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Merchant needs to set Address to determine Tax");
			} else {
				subs.setTax(pricing.getInterstateTax());
			}
		} else {
			if (CommonUtil.isNull(adset.getAddress()) || CommonUtil.isEmpty(adset.getAddress().getState())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Admin needs to set Address to determine Tax");
			}
			if (adset.getAddress().getState().equalsIgnoreCase(merchant.getAddress().getState())) {
				subs.setTax(pricing.getIntrastateTax());
			} else {
				subs.setTax(pricing.getInterstateTax());
			}
		}
		BigDecimal total = pricing.getRate().multiply(BigDecimal.valueOf(offline.getQuantity()));
		subs.setTotal(total);
		BigDecimal payAmount = total
				.add(total.multiply(BigDecimal.valueOf(subs.getTax().getValue())).divide(BigDecimal.valueOf(100)));
		subs.setPayAmount(payAmount);
		subs.setCurrency(Currency.INR);
		subs.setCreated(timeNow);
		subs.setMerchant(merchant);
		subs.setPricing(pricing);
		subs.setQuantity(offline.getQuantity());
		subs.setPaymentRefNo(offline.getPaymentRefNo());
		subs.setStatus("captured");
		String charset = hmacSigner.signWithSecretKey(merchant.getSecretKey(), String.valueOf(timeNow.getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String subsCode = "";
		do {
			subsCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
		} while ("".equals(subsCode) || CommonUtil.isNotNull(subsRepo.findBySubscriptionCode(subsCode)));
		subs.setSubscriptionCode(subsCode);
		subs.setPayMode(offline.getPayMode());
		subs.setMethod(offline.getMethod());
		subsRepo.save(subs);
		MerchantPricing merPricing = new MerchantPricing();
		merPricing.setCreated(timeNow);
		merPricing.setStartDate(timeNow);
		merPricing.setEndDate(DateUtil.getExpiry(timeNow, pricing.getDuration()));
		merPricing.setPricing(pricing);
		merPricing.setQuantity(offline.getQuantity());
		merPricing.setStatus(PricingStatus.ACTIVE);
		merPricing.setUseCount(0);
		merPricing.setMerchant(merchant);
		merPricing.setSubscription(subs);
		merPriRepo.save(merPricing);
		addToExpense(merchant, subs);
	}

	public ModelAndView onlineSubscription(Integer pricingId, Integer quantity, Merchant merchant) {
		logger.info("Online subscription request for pricingId : {}, quantity : {} by merchant : {}", pricingId,
				quantity, merchant.getId());
		Date timeNow = new Date();
		Pricing pricing = priRepo.findOne(pricingId);
		AdminSetting adset = adsetRepo.findAll().get(0);
		if (!pricing.isActive() || BigDecimal.ZERO.compareTo(pricing.getRate()) > -1 || CommonUtil.isNull(quantity)
				|| quantity <= 0) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Bad Request");
		}
		Subscription subs = new Subscription();
		if (CommonUtil.isNull(merchant.getAddress()) || CommonUtil.isEmpty(merchant.getAddress().getState())) {
			if (!"NO_TAX".equalsIgnoreCase(pricing.getInterstateTax().getName())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Merchant needs to set Address to determine Tax");
			} else {
				subs.setTax(pricing.getInterstateTax());
			}
		} else {
			if (CommonUtil.isNull(adset.getAddress()) || CommonUtil.isEmpty(adset.getAddress().getState())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Something went wrong, please try again");
			}
			if (adset.getAddress().getState().equalsIgnoreCase(merchant.getAddress().getState())) {
				subs.setTax(pricing.getIntrastateTax());
			} else {
				subs.setTax(pricing.getInterstateTax());
			}
		}
		if (CommonUtil.isNull(adset.getPaymentSetting())
				|| CommonUtil.isEmpty(adset.getPaymentSetting().getRzpMerchantId())
				|| CommonUtil.isEmpty(adset.getPaymentSetting().getRzpKeyId())
				|| CommonUtil.isEmpty(adset.getPaymentSetting().getRzpSecretId())) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Online Payment not available");
		}
		BigDecimal total = pricing.getRate().multiply(BigDecimal.valueOf(quantity));
		subs.setTotal(total);
		BigDecimal payAmount = total
				.add(total.multiply(BigDecimal.valueOf(subs.getTax().getValue())).divide(BigDecimal.valueOf(100)));
		subs.setPayAmount(payAmount);
		subs.setCurrency(Currency.INR);
		subs.setPayMode(PayMode.PAYCR);
		subs.setQuantity(quantity);
		subs.setCreated(timeNow);
		subs.setMerchant(merchant);
		subs.setPricing(pricing);
		subs.setStatus("initiated");

		String charset = hmacSigner.signWithSecretKey(merchant.getSecretKey(), String.valueOf(timeNow.getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String subsCode = "";
		do {
			subsCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
		} while ("".equals(subsCode) || CommonUtil.isNotNull(subsRepo.findBySubscriptionCode(subsCode)));
		subs.setSubscriptionCode(subsCode);
		subsRepo.save(subs);
		ModelAndView mv = new ModelAndView("html/subscribe");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("webUrl", company.getWebUrl());
		mv.addObject("merchant", merchant);
		mv.addObject("banner", company.getAppUrl() + "/banner/admin/" + adset.getBanner());
		mv.addObject("pricing", pricing);
		mv.addObject("subs", subs);
		mv.addObject("signature", hmacSigner.signWithSecretKey(merchant.getSecretKey(), subsCode));
		mv.addObject("rzpKeyId", adset.getPaymentSetting().getRzpKeyId());
		mv.addObject("payAmount", String.valueOf(subs.getPayAmount().multiply(BigDecimal.valueOf(100))));
		return mv;
	}

	public Subscription purchase(Map<String, String> formData) throws Exception {
		logger.info("Purchase response received for subscription : {}", new Gson().toJson(formData));
		Date timeNow = new Date();
		AdminSetting adset = adsetRepo.findAll().get(0);
		ModelAndView mv = new ModelAndView("html/subs-response");
		mv.addObject("staticUrl", company.getStaticUrl());
		String rzpPayId = formData.get("razorpay_payment_id");
		String subsCode = formData.get("subsCode");
		String signature = formData.get("signature");
		Subscription subs = subsRepo.findBySubscriptionCode(subsCode);
		if ("captured".equals(subs.getStatus())) {
			return subs;
		}
		Merchant merchant = subs.getMerchant();
		if (!hmacSigner.signWithSecretKey(merchant.getSecretKey(), subsCode).equals(signature)) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Signature mismatch");
		}
		Pricing pricing = subs.getPricing();
		RazorpayClient razorpay = new RazorpayClient(adset.getPaymentSetting().getRzpKeyId(),
				adset.getPaymentSetting().getRzpSecretId());
		com.razorpay.Payment rzpPayment = razorpay.Payments.fetch(rzpPayId);
		JSONObject request = new JSONObject();
		request.put("amount", rzpPayment.get("amount").toString());
		if ("authorized".equals(rzpPayment.get("status"))) {
			rzpPayment = razorpay.Payments.capture(rzpPayId, request);
		}
		subs.setPaymentRefNo(rzpPayId);
		subs.setStatus(rzpPayment.get("status"));
		StringBuilder method = new StringBuilder(rzpPayment.get("method"));
		method.append(JSONObject.NULL.equals(rzpPayment.get("bank")) ? "" : " - " + rzpPayment.get("bank"));
		method.append(JSONObject.NULL.equals(rzpPayment.get("wallet")) ? "" : " - " + rzpPayment.get("wallet"));
		subs.setMethod(method.toString());
		subsRepo.save(subs);
		mv.addObject("subs", subs);
		if ("captured".equals(subs.getStatus())) {
			MerchantPricing merPricing = new MerchantPricing();
			merPricing.setCreated(timeNow);
			merPricing.setStartDate(timeNow);
			merPricing.setEndDate(DateUtil.getExpiry(timeNow, pricing.getDuration()));
			merPricing.setPricing(pricing);
			merPricing.setQuantity(subs.getQuantity());
			merPricing.setStatus(PricingStatus.ACTIVE);
			merPricing.setUseCount(0);
			merPricing.setMerchant(merchant);
			merPricing.setSubscription(subs);
			merPriRepo.save(merPricing);

			Notification noti = new Notification();
			noti.setMerchantId(merchant.getId());
			noti.setMessage("New Subscription : " + pricing.getName());
			noti.setSubject("Congratulation!");
			noti.setCreated(timeNow);
			noti.setRead(false);
			notiRepo.save(noti);

			addToExpense(merchant, subs);
		}
		return subs;
	}

	private void addToExpense(Merchant merchant, Subscription subs) {
		if (subs.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		logger.info("Adding subscription : {} to expense for merchant : {}", subs.getId(), merchant.getId());
		String createdBy = "SYSTEM";
		Date timeNow = new Date();
		AdminSetting adset = adsetRepo.findAll().get(0);
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
		supplier.setName(company.getName());
		supplier.setEmail(company.getEmail());
		supplier.setMobile(company.getMobile());
		supplier.setGstin(adset.getGstin());
		if (CommonUtil.isNotNull(adset.getAddress())) {
			Address addr = new Address();
			addr.setAddressLine1(adset.getAddress().getAddressLine1());
			addr.setAddressLine2(adset.getAddress().getAddressLine2());
			addr.setCity(adset.getAddress().getCity());
			addr.setState(adset.getAddress().getState());
			addr.setPincode(adset.getAddress().getPincode());
			addr.setCountry(adset.getAddress().getCountry());
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

	public File downloadPdf(String subsCode) throws IOException {
		logger.info("Download subscription request for code : {}", subsCode);
		String pdfPath = server.getSubscriptionLocation() + "/receipt" + subsCode + ".pdf";
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()) {
			return pdfFile;
		}
		pdfFile.createNewFile();
		pdfUtil.makePdf(company.getAppUrl() + "/subscription/receipt/" + subsCode, pdfFile.getAbsolutePath());
		return pdfFile;
	}

	public void decline(String subsCode) {
		logger.info("Decline subscription request for code : {}", subsCode);
		Subscription subs = subsRepo.findBySubscriptionCode(subsCode);
		subs.setStatus("declined");
		subsRepo.save(subs);
	}

	public ModelAndView getSubscriptionReceipt(String subsCode) {
		logger.info("Receipt for subscription request for code : {}", subsCode);
		AdminSetting adset = adsetRepo.findAll().get(0);
		Subscription subs = getSubscriptionByCode(subsCode);
		ModelAndView mv = new ModelAndView("receipt/subscription");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("subs", subs);
		mv.addObject("admin", adset);
		mv.addObject("company", company);
		return mv;
	}
}
