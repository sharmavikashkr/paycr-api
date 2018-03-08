package com.paycr.dashboard.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
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
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.SubscriptionRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.Currency;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PricingStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.PdfUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.paycr.dashboard.helper.SubscriptionHelper;
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
	private SubscriptionHelper subsHelp;

	@Autowired
	private SubscriptionRepository subsRepo;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Autowired
	private NotificationRepository notiRepo;

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
		Merchant paycr = merRepo.findOne(company.getMerchantId());
		Subscription subs = new Subscription();
		if (CommonUtil.isNull(merchant.getAddress()) || CommonUtil.isEmpty(merchant.getAddress().getState())) {
			if (!"NO_TAX".equalsIgnoreCase(pricing.getInterstateTax().getName())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Merchant needs to set Address to determine Tax");
			} else {
				subs.setTax(pricing.getInterstateTax());
			}
		} else {
			if (CommonUtil.isNull(paycr.getAddress()) || CommonUtil.isEmpty(paycr.getAddress().getState())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Admin needs to set Address to determine Tax");
			}
			if (paycr.getAddress().getState().equalsIgnoreCase(merchant.getAddress().getState())) {
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
		subsHelp.addToExpense(merchant.getId(), subs);
		subsHelp.addToInvoice(merchant.getId(), subs);
	}

	public ModelAndView onlineSubscription(Integer pricingId, Integer quantity, Merchant merchant) {
		logger.info("Online subscription request for pricingId : {}, quantity : {} by merchant : {}", pricingId,
				quantity, merchant.getId());
		Date timeNow = new Date();
		Pricing pricing = priRepo.findOne(pricingId);
		Merchant paycr = merRepo.findOne(company.getMerchantId());
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
			if (CommonUtil.isNull(paycr.getAddress()) || CommonUtil.isEmpty(paycr.getAddress().getState())) {
				throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Online Payment not available");
			}
			if (paycr.getAddress().getState().equalsIgnoreCase(merchant.getAddress().getState())) {
				subs.setTax(pricing.getIntrastateTax());
			} else {
				subs.setTax(pricing.getInterstateTax());
			}
		}
		if (CommonUtil.isNull(paycr.getPaymentSetting())
				|| CommonUtil.isEmpty(paycr.getPaymentSetting().getRzpMerchantId())
				|| CommonUtil.isEmpty(paycr.getPaymentSetting().getRzpKeyId())
				|| CommonUtil.isEmpty(paycr.getPaymentSetting().getRzpSecretId())) {
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
		mv.addObject("banner",
				company.getAppUrl() + "/banner/merchant/" + paycr.getAccessKey() + "/" + paycr.getBanner());
		mv.addObject("pricing", pricing);
		mv.addObject("subs", subs);
		mv.addObject("signature", hmacSigner.signWithSecretKey(merchant.getSecretKey(), subsCode));
		mv.addObject("rzpKeyId", paycr.getPaymentSetting().getRzpKeyId());
		mv.addObject("payAmount", String.valueOf(subs.getPayAmount().multiply(BigDecimal.valueOf(100))));
		return mv;
	}

	public Subscription purchase(Map<String, String> formData) throws Exception {
		logger.info("Purchase response received for subscription : {}", new Gson().toJson(formData));
		Date timeNow = new Date();
		Merchant paycr = merRepo.findOne(company.getMerchantId());
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
		RazorpayClient razorpay = new RazorpayClient(paycr.getPaymentSetting().getRzpKeyId(),
				paycr.getPaymentSetting().getRzpSecretId());
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

			subsHelp.addToExpense(merchant.getId(), subs);
			subsHelp.addToInvoice(merchant.getId(), subs);
		}
		return subs;
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
		Merchant paycr = merRepo.findOne(company.getMerchantId());
		Subscription subs = getSubscriptionByCode(subsCode);
		ModelAndView mv = new ModelAndView("receipt/subscription");
		mv.addObject("staticUrl", company.getStaticUrl());
		mv.addObject("subs", subs);
		mv.addObject("admin", paycr);
		mv.addObject("company", company);
		return mv;
	}
}
