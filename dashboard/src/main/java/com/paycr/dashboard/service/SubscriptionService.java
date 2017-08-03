package com.paycr.dashboard.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.OfflineSubscription;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.domain.SubscriptionMode;
import com.paycr.common.data.repository.MerchantPricingRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.SubscriptionModeRepository;
import com.paycr.common.data.repository.SubscriptionRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.type.Currency;
import com.paycr.common.type.PayMode;
import com.paycr.common.type.PricingStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

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
	private SubscriptionModeRepository subsModeRepo;

	public List<SubscriptionMode> getSubscriptionModes() {
		return subsModeRepo.findAll();
	}

	public Subscription getSubscription(Integer subscriptionId) {
		Subscription subs = subsRepo.findOne(subscriptionId);
		return subs;
	}

	public void createSubscriptionSetting(SubscriptionMode subsMode) {
		if (CommonUtil.isNull(subsMode) || CommonUtil.isEmpty(subsMode.getRzpMerchantId())
				|| CommonUtil.isEmpty(subsMode.getRzpKeyId()) || CommonUtil.isEmpty(subsMode.getRzpSecretId())) {
			throw new PaycrException(Constants.FAILURE, "Invalid Request");
		}
		SubscriptionMode existMode = subsModeRepo.findByActiveAndPayMode(true, subsMode.getPayMode());
		if (existMode != null && subsMode.isActive()) {
			existMode.setActive(false);
			subsModeRepo.save(existMode);
		}
		subsModeRepo.save(subsMode);
	}

	public void toggleSubscriptionSetting(Integer modeId) {
		SubscriptionMode toggleMode = subsModeRepo.findOne(modeId);
		SubscriptionMode existMode = subsModeRepo.findByActiveAndPayMode(true, toggleMode.getPayMode());
		if (toggleMode != null && existMode != null) {
			existMode.setActive(false);
			subsModeRepo.save(existMode);
		}
		toggleMode.setActive(true);
		subsModeRepo.save(toggleMode);
	}

	public void offlineSubscription(OfflineSubscription offline) {
		Date timeNow = new Date();
		SubscriptionMode subsMode = subsModeRepo.findOne(offline.getSubscriptionModeId());
		Merchant merchant = merRepo.findOne(offline.getMerchantId());
		Pricing pricing = priRepo.findOne(offline.getPricingId());
		Subscription subs = new Subscription();
		subs.setAmount(pricing.getRate());
		subs.setCurrency(Currency.INR);
		subs.setCreated(timeNow);
		subs.setMerchant(merchant);
		subs.setPricing(pricing);
		subs.setQuantity(offline.getQuantity());
		subs.setPaymentRefNo(offline.getPaymentRefNo());
		subs.setMethod(subsMode.getName());
		subs.setStatus("SUCCESS");
		subs.setSubscriptionCode("OFFLINE");
		subs.setSubscriptionMode(subsMode);
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
		if (!pricing.isActive() || new BigDecimal(0).compareTo(pricing.getRate()) > -1 || quantity == 0
				|| quantity == null) {
			throw new PaycrException(Constants.FAILURE, "Bad Request");
		}
		Subscription subs = new Subscription();
		subs.setAmount(pricing.getRate().multiply(new BigDecimal(quantity)));
		subs.setCurrency(Currency.INR);
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
		SubscriptionMode subsMode = subsModeRepo.findByActiveAndPayMode(true, PayMode.PAYCR);
		if (CommonUtil.isNull(subsMode)) {
			throw new PaycrException(Constants.FAILURE, "Not Allowed");
		}
		subs.setSubscriptionMode(subsMode);
		subsRepo.save(subs);
		ModelAndView mv = new ModelAndView("html/subscribe");
		mv.addObject("merchant", merchant);
		mv.addObject("pricing", pricing);
		mv.addObject("subs", subs);
		mv.addObject("rzpKeyId", subsMode.getRzpKeyId());
		mv.addObject("payAmount", String.valueOf(subs.getAmount().multiply(new BigDecimal(100))));
		return mv;
	}

	public ModelAndView purchase(Map<String, String> formData) throws IOException, RazorpayException {
		Date timeNow = new Date();
		String subsCode = null;
		ModelAndView mv = new ModelAndView("html/subs-response");
		String rzpPayId = formData.get("razorpay_payment_id");
		subsCode = formData.get("subsCode");
		Subscription subs = subsRepo.findBySubscriptionCode(subsCode);
		if ("captured".equals(subs.getStatus())) {
			return mv;
		}
		Merchant merchant = subs.getMerchant();
		Pricing pricing = subs.getPricing();
		SubscriptionMode subsMode = subs.getSubscriptionMode();
		RazorpayClient razorpay = new RazorpayClient(subsMode.getRzpKeyId(), subsMode.getRzpSecretId());
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

			mv.addObject("status", "SUCCESS");
			mv.addObject("success", true);
		} else {
			mv.addObject("status", "FAILURE");
			mv.addObject("success", false);
		}
		return mv;
	}
}
