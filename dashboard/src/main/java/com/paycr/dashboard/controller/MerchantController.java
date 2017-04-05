package com.paycr.dashboard.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.MerchantCustomParam;
import com.paycr.common.data.domain.MerchantSetting;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.ParamValueProvider;
import com.paycr.common.util.DateUtil;
import com.paycr.dashboard.service.MerchantService;

@Secured({ "ROLE_MERCHANT" })
@RestController
@RequestMapping("/merchant")
public class MerchantController {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private MerchantService merSer;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private PricingRepository priceRepo;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private PaymentRepository payRepo;

	@RequestMapping("")
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

	@RequestMapping("/customParam/new")
	public String newCustomParam(@RequestBody MerchantCustomParam customParam, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			return merSer.newCustomParam(merchant, customParam);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return "FAILURE";
		}
	}

	@RequestMapping("/customParam/delete/{id}")
	public String deleteCustomParam(@PathVariable Integer id, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			return merSer.deleteCustomParam(merchant, id);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return ex.getMessage();
		}
	}

	@RequestMapping("/setting/update")
	public String resetSendSms(@RequestBody MerchantSetting setting, HttpServletResponse response) {
		try {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			return merSer.updateSetting(merchant, setting);
		} catch (Exception ex) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			return "FAILURE";
		}
	}

}
