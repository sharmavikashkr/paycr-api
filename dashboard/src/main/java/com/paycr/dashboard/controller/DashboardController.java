package com.paycr.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantUser;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.MerchantRepository;
import com.paycr.common.data.repository.MerchantUserRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.data.repository.UserRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;

@RestController
public class DashboardController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private PricingRepository priceRepo;

	@Autowired
	private MerchantRepository merRepo;

	@Autowired
	private MerchantUserRepository merUserRepo;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private PaymentRepository payRepo;

	@Autowired
	private BCryptPasswordEncoder bcPassEncode;

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("html/index");
	}

	@RequestMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("html/login");
	}

	@RequestMapping(value = "/app/login", method = RequestMethod.POST)
	public String appLogin(@RequestParam(value = "username", required = true) String email,
			@RequestParam(value = "password", required = true) String password,
			@RequestHeader(value = "accessKey", required = true) String accessKey) {
		PcUser user = userRepo.findByEmail(email);
		if (CommonUtil.isNull(user)) {
			throw new PaycrException(Constants.FAILURE, "We do not recognize you");
		}
		if (!bcPassEncode.encode(password).equals(user.getPassword())) {
			throw new PaycrException(Constants.FAILURE, "We do not recognize you");
		}
		MerchantUser merUser = merUserRepo.findByUserId(user.getId());
		if (CommonUtil.isNull(merUser)) {
			throw new PaycrException(Constants.FAILURE, "We do not recognize you");
		}
		Merchant merchant = merRepo.findOne(merUser.getMerchantId());
		if (CommonUtil.isNull(merchant)) {
			throw new PaycrException(Constants.FAILURE, "We do not recognize you");
		}
		if(!accessKey.equals(merchant.getAccessKey())) {
			throw new PaycrException(Constants.FAILURE, "We do not recognize you");
		}
		return merchant.getSecretKey();
	}

	@RequestMapping("/forgotPassword")
	public ModelAndView forgotPasssword(@RequestParam(value = "error", required = false) String code) {
		ModelAndView mv = new ModelAndView("html/forgot-password");
		String message = "Enter Email to send reset password link";
		boolean isError = false;
		if ("1".equals(code)) {
			message = "User not registered";
			isError = true;
		} else if ("2".equals(code)) {
			message = "Reset already requested 3 times in 24 hours";
			isError = true;
		}
		mv.addObject("message", message);
		mv.addObject("isError", isError);
		return mv;
	}

	@Secured({ "ROLE_MERCHANT" })
	@RequestMapping("/dashboard")
	public ModelAndView dashboard() {
		PcUser user = secSer.findLoggedInUser();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		Pageable topTwenty = new PageRequest(0, 20);
		List<Invoice> invoices = invRepo.findByMerchantOrderByIdDesc(merchant.getId(), topTwenty);
		for (Invoice invoice : invoices) {
			if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
				invoice.setPaid(true);
			} else {
				invoice.setPaid(false);
			}
			invoice.setAllPayments(payRepo.findByInvoiceCode(invoice.getInvoiceCode()));
		}
		Pageable topFour = new PageRequest(0, 4);
		List<Notification> notices = notiRepo.findByUserIdOrMerchantIdOrderByIdDesc(null, merchant.getId(), topFour);
		for (Notification notice : notices) {
			notice.setCreatedStr(DateUtil.getDashboardDate(notice.getCreated()));
		}
		List<Pricing> pricings = priceRepo.findAll();
		ModelAndView mv = new ModelAndView("html/dashboard");
		mv.addObject("user", user);
		mv.addObject("merchant", merchant);
		mv.addObject("pricings", pricings);
		mv.addObject("provider", ParamValueProvider.values());
		mv.addObject("invoices", invoices);
		mv.addObject("notices", notices);
		return mv;
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/admin")
	public ModelAndView admin() {
		PcUser user = secSer.findLoggedInUser();
		ModelAndView mv = new ModelAndView("html/admin");
		mv.addObject("user", user);
		Pageable topFour = new PageRequest(0, 4);
		List<Notification> notices = notiRepo.findByUserIdOrMerchantIdOrderByIdDesc(user.getId(), null, topFour);
		for (Notification notice : notices) {
			notice.setCreatedStr(DateUtil.getDashboardDate(notice.getCreated()));
		}
		List<Pricing> pricings = priceRepo.findAll();
		mv.addObject("pricings", pricings);
		List<Merchant> merchants = merRepo.findAll();
		mv.addObject("merchants", merchants);
		mv.addObject("notices", notices);
		return mv;
	}
}
