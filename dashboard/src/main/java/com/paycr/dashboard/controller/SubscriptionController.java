package com.paycr.dashboard.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantPricing;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.data.domain.SubscriptionSetting;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.SubscriptionRepository;
import com.paycr.common.data.repository.SubscriptionSettingRepository;
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
	private SubscriptionSettingRepository subsSetRepo;

	@Secured({ "ROLE_MERCHANT" })
	@RequestMapping("/new/{pricingId}")
	public ModelAndView newSubscription(@PathVariable Integer pricingId) {
		Date timeNow = new Date();
		Pricing pricing = priRepo.findOne(pricingId);
		if (!pricing.isActive() || new BigDecimal(0).compareTo(pricing.getRate()) > -1) {
			throw new PaycrException(Constants.FAILURE, "Not Allowed");
		}
		Merchant merchant = secSer.getMerchantForLoggedInUser();
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
		subsRepo.save(subs);
		SubscriptionSetting subsSet = subsSetRepo.findByActive(true);
		if (CommonUtil.isNull(subsSet)) {
			throw new PaycrException(Constants.FAILURE, "Not Allowed");
		}
		ModelAndView mv = new ModelAndView("html/subscribe");
		mv.addObject("merchant", merchant);
		mv.addObject("pricing", pricing);
		mv.addObject("subsCode", subsCode);
		mv.addObject("rzpKeyId", subsSet.getRzpKeyId());
		mv.addObject("payAmount", subs.getAmount().multiply(new BigDecimal(100)));
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
			SubscriptionSetting subsSet = subsSetRepo.findByActive(true);
			if (CommonUtil.isNull(subsSet)) {
				throw new PaycrException(Constants.FAILURE, "Not Allowed");
			}
			RazorpayClient razorpay = new RazorpayClient(subsSet.getRzpKeyId(), subsSet.getRzpSecretId());
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
				List<MerchantPricing> merPricings = new ArrayList<MerchantPricing>();
				MerchantPricing merPricing = new MerchantPricing();
				merPricing.setCreated(timeNow);
				merPricing.setStartDate(timeNow);
				merPricing.setEndDate(DateUtil.getExpiry(timeNow, pricing.getDuration()));
				merPricing.setPricing(pricing);
				merPricing.setStatus(PricingStatus.ACTIVE);
				merPricing.setMerchant(merchant);
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
