package com.paycr.dashboard.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.bean.OfflineSubscription;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.domain.SubscriptionMode;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.SubscriptionModeRepository;
import com.paycr.common.data.repository.SubscriptionRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.Currency;
import com.paycr.common.type.PricingStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;
import com.paycr.common.util.HmacSignerUtil;
import com.paycr.common.util.RandomIdGenerator;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private PricingRepository priRepo;

	@Autowired
	private SubscriptionRepository subsRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private HmacSignerUtil hmacSigner;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private SubscriptionModeRepository subsModeRepo;

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/modes")
	public List<SubscriptionMode> getSubscriptionModes() {
		return subsModeRepo.findAll();
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/mode/new")
	public void createSubscriptionSetting(@RequestBody SubscriptionMode subsMode, HttpServletResponse response) {
		try {
			if (CommonUtil.isNull(subsMode) || CommonUtil.isEmpty(subsMode.getRzpMerchantId())
					|| CommonUtil.isEmpty(subsMode.getRzpKeyId()) || CommonUtil.isEmpty(subsMode.getRzpSecretId())) {
				throw new PaycrException(Constants.FAILURE, "Invalid Request");
			}
			SubscriptionMode existMode = subsModeRepo.findByActiveAndName(true, subsMode.getName());
			if (existMode != null && subsMode.isActive()) {
				existMode.setActive(false);
				subsModeRepo.save(existMode);
			}
			subsModeRepo.save(subsMode);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
		}
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMIN_USER')")
	@RequestMapping("/get/{subscriptionId}")
	public Subscription getSubscription(@PathVariable Integer subscriptionId, HttpServletResponse response) {
		try {
			Subscription subs = subsRepo.findOne(subscriptionId);
			return subs;
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/mode/toggle/{modeId}")
	public void toggleSubscriptionSetting(@PathVariable Integer modeId, HttpServletResponse response) {
		try {
			SubscriptionMode toggleMode = subsModeRepo.findOne(modeId);
			SubscriptionMode existMode = subsModeRepo.findByActiveAndName(true, toggleMode.getName());
			if (toggleMode != null && existMode != null) {
				existMode.setActive(false);
				subsModeRepo.save(existMode);
			}
			toggleMode.setActive(true);
			subsModeRepo.save(toggleMode);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
		}
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping("/new/offline")
	public void offlineSubscription(@RequestBody OfflineSubscription offline, HttpServletResponse response) {
		try {
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
			subs.setStatus("SUCCESS");
			subs.setSubscriptionCode("OFFLINE");
			subs.setSubscriptionMode(subsMode);
			subsRepo.save(subs);
			List<MerchantPricing> merPricings = merchant.getPricings();
			if (merPricings == null) {
				merPricings = new ArrayList<MerchantPricing>();
			}
			MerchantPricing merPricing = new MerchantPricing();
			merPricing.setCreated(timeNow);
			merPricing.setStartDate(timeNow);
			merPricing.setEndDate(DateUtil.getExpiry(timeNow, pricing.getDuration()));
			merPricing.setPricing(pricing);
			merPricing.setStatus(PricingStatus.ACTIVE);
			merPricing.setMerchant(merchant);
			merPricing.setSubscription(subs);
			merPricings.add(merPricing);
			merchant.setPricings(merPricings);
			merRepo.save(merchant);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
		}
	}

	@RequestMapping("/new/online/{pricingId}")
	public ModelAndView onlineSubscription(@PathVariable Integer pricingId, HttpServletRequest request) {
		Date timeNow = new Date();
		Pricing pricing = priRepo.findOne(pricingId);
		if (!pricing.isActive() || new BigDecimal(0).compareTo(pricing.getRate()) > -1) {
			throw new PaycrException(Constants.FAILURE, "Not Allowed");
		}
		String token = null;
		for (Cookie cookie : request.getCookies()) {
			if ("access_token".equals(cookie.getName())) {
				token = cookie.getValue();
			}
		}
		if (CommonUtil.isNull(token)) {
			throw new PaycrException(Constants.FAILURE, "We do not recognize you");
		}
		Merchant merchant = secSer.getMerchantForLoggedInUser(token);
		if (CommonUtil.isNull(merchant)) {
			throw new PaycrException(Constants.FAILURE, "We do not recognize you");
		}
		Subscription subs = new Subscription();
		subs.setAmount(pricing.getRate());
		subs.setCurrency(Currency.INR);
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
		SubscriptionMode subsMode = subsModeRepo.findByActiveAndName(true, "PAYCR");
		if (CommonUtil.isNull(subsMode)) {
			throw new PaycrException(Constants.FAILURE, "Not Allowed");
		}
		subs.setSubscriptionMode(subsMode);
		subsRepo.save(subs);
		ModelAndView mv = new ModelAndView("html/subscribe");
		mv.addObject("merchant", merchant);
		mv.addObject("pricing", pricing);
		mv.addObject("subsCode", subsCode);
		mv.addObject("rzpKeyId", subsMode.getRzpKeyId());
		mv.addObject("payAmount", String.valueOf(subs.getAmount().multiply(new BigDecimal(100))));
		return mv;
	}

	@RequestMapping(value = "/return", method = RequestMethod.POST)
	public ModelAndView purchase(@RequestParam Map<String, String> formData, HttpServletResponse response)
			throws IOException {
		Date timeNow = new Date();
		String subsCode = null;
		ModelAndView mv = new ModelAndView("html/subs-response");
		try {
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
				List<MerchantPricing> merPricings = merchant.getPricings();
				if (merPricings == null) {
					merPricings = new ArrayList<MerchantPricing>();
				}
				MerchantPricing merPricing = new MerchantPricing();
				merPricing.setCreated(timeNow);
				merPricing.setStartDate(timeNow);
				merPricing.setEndDate(DateUtil.getExpiry(timeNow, pricing.getDuration()));
				merPricing.setPricing(pricing);
				merPricing.setStatus(PricingStatus.ACTIVE);
				merPricing.setMerchant(merchant);
				merPricing.setSubscription(subs);
				merPricings.add(merPricing);
				merchant.setPricings(merPricings);
				merRepo.save(merchant);

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
		} catch (RazorpayException e) {
			System.out.println(e.getMessage());
			mv.addObject("status", "FAILURE");
			mv.addObject("success", false);
		}
		return mv;
	}

}
