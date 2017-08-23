package com.paycr.dashboard.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.OfflineSubscription;
import com.paycr.common.bean.Server;
import com.paycr.common.data.domain.AdminSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.repository.AdminSettingRepository;
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
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.PdfUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.razorpay.RazorpayClient;

@Service
public class SubscriptionService {

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private MerchantPricingRepository merPriRepo;

	@Autowired
	private PricingRepository priRepo;

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
		return subsRepo.findBySubscriptionCode(subscriptionCode);
	}

	public Subscription getSubscription(Integer pricingId) {
		MerchantPricing merPricing = merPriRepo.findOne(pricingId);
		return merPricing.getSubscription();
	}

	public void offlineSubscription(OfflineSubscription offline) {
		Date timeNow = new Date();
		Merchant merchant = merRepo.findOne(offline.getMerchantId());
		Pricing pricing = priRepo.findOne(offline.getPricingId());
		AdminSetting adset = adsetRepo.findAll().get(0);
		Subscription subs = new Subscription();
		BigDecimal total = pricing.getRate().multiply(new BigDecimal(offline.getQuantity()));
		subs.setTotal(total);
		subs.setTaxName(adset.getTaxName());
		subs.setTaxValue(adset.getTaxValue());
		BigDecimal payAmount = total
				.add(total.multiply(new BigDecimal(adset.getTaxValue())).divide(new BigDecimal(100)));
		subs.setPayAmount(payAmount);
		subs.setCurrency(Currency.INR);
		subs.setCreated(timeNow);
		subs.setMerchant(merchant);
		subs.setPricing(pricing);
		subs.setQuantity(offline.getQuantity());
		subs.setPaymentRefNo(offline.getPaymentRefNo());
		subs.setMethod(offline.getPayMode().name());
		subs.setStatus("captured");
		String charset = hmacSigner.signWithSecretKey(merchant.getSecretKey(), String.valueOf(timeNow.getTime()));
		charset += charset.toLowerCase() + charset.toUpperCase();
		String subsCode = "";
		do {
			subsCode = RandomIdGenerator.generateInvoiceCode(charset.toCharArray());
		} while ("".equals(subsCode) || CommonUtil.isNotNull(subsRepo.findBySubscriptionCode(subsCode)));
		subs.setSubscriptionCode(subsCode);
		subs.setPayMode(offline.getPayMode());
		subsRepo.save(subs);
		MerchantPricing merPricing = new MerchantPricing();
		merPricing.setCreated(timeNow);
		merPricing.setStartDate(timeNow);
		merPricing.setEndDate(DateUtil.getExpiry(timeNow, pricing.getDuration()));
		merPricing.setPricing(pricing);
		merPricing.setQuantity(offline.getQuantity());
		merPricing.setStatus(PricingStatus.ACTIVE);
		merPricing.setInvCount(0);
		merPricing.setMerchant(merchant);
		merPricing.setSubscription(subs);
		merPriRepo.save(merPricing);
	}

	public ModelAndView onlineSubscription(Integer pricingId, Integer quantity, Merchant merchant) {
		Date timeNow = new Date();
		Pricing pricing = priRepo.findOne(pricingId);
		AdminSetting adset = adsetRepo.findAll().get(0);
		if (!pricing.isActive() || new BigDecimal(0).compareTo(pricing.getRate()) > -1 || quantity == null
				|| quantity <= 0) {
			throw new PaycrException(Constants.FAILURE, "Bad Request");
		}
		Subscription subs = new Subscription();
		BigDecimal total = pricing.getRate().multiply(new BigDecimal(quantity));
		subs.setTotal(total);
		subs.setTaxName(adset.getTaxName());
		subs.setTaxValue(adset.getTaxValue());
		BigDecimal payAmount = total
				.add(total.multiply(new BigDecimal(adset.getTaxValue())).divide(new BigDecimal(100)));
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
		mv.addObject("merchant", merchant);
		mv.addObject("banner", company.getBaseUrl() + "/banner/admin/" + adset.getBanner());
		mv.addObject("pricing", pricing);
		mv.addObject("subs", subs);
		mv.addObject("rzpKeyId", adset.getPaymentSetting().getRzpKeyId());
		mv.addObject("payAmount", String.valueOf(subs.getPayAmount().multiply(new BigDecimal(100))));
		return mv;
	}

	public Subscription purchase(Map<String, String> formData) throws Exception {
		Date timeNow = new Date();
		AdminSetting adset = adsetRepo.findAll().get(0);
		String subsCode = null;
		ModelAndView mv = new ModelAndView("html/subs-response");
		String rzpPayId = formData.get("razorpay_payment_id");
		subsCode = formData.get("subsCode");
		Subscription subs = subsRepo.findBySubscriptionCode(subsCode);
		if ("captured".equals(subs.getStatus())) {
			return subs;
		}
		Merchant merchant = subs.getMerchant();
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
		subs.setMethod(rzpPayment.get("method"));
		subs.setBank(JSONObject.NULL.equals(rzpPayment.get("bank")) ? null : rzpPayment.get("bank"));
		subs.setWallet(JSONObject.NULL.equals(rzpPayment.get("wallet")) ? null : rzpPayment.get("wallet"));
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
			merPricing.setInvCount(0);
			merPricing.setMerchant(merchant);
			merPricing.setSubscription(subs);
			merPriRepo.save(merPricing);

			Notification noti = new Notification();
			noti.setMerchantId(merchant.getId());
			noti.setMessage("New Pricing plan subscribed : " + pricing.getName());
			noti.setSubject("Congratulation!");
			noti.setCreated(timeNow);
			noti.setRead(false);
			notiRepo.save(noti);
		}
		return subs;
	}

	public File downloadPdf(String subscriptionCode) throws IOException {
		String pdfPath = server.getSubscriptionLocation() + subscriptionCode + ".pdf";
		File pdfFile = new File(pdfPath);
		if (pdfFile.exists()) {
			return pdfFile;
		}
		pdfFile.createNewFile();
		pdfUtil.makePdf(company.getBaseUrl() + "/subscription/response/" + subscriptionCode + "?show=false",
				pdfFile.getAbsolutePath());
		return pdfFile;
	}

	public void decline(String subsCode) {
		Subscription subs = subsRepo.findBySubscriptionCode(subsCode);
		subs.setStatus("declined");
		subsRepo.save(subs);
	}
}
