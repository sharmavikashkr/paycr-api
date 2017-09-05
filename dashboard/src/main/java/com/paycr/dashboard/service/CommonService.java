package com.paycr.dashboard.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.DailyPay;
import com.paycr.common.bean.SearchInvoiceRequest;
import com.paycr.common.bean.StatsRequest;
import com.paycr.common.bean.StatsResponse;
import com.paycr.common.data.dao.InvoiceDao;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Notification;
import com.paycr.common.data.domain.Payment;
import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.Pricing;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.data.repository.NotificationRepository;
import com.paycr.common.data.repository.PaymentRepository;
import com.paycr.common.data.repository.PricingRepository;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.PayType;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;

@Service
public class CommonService {

	@Autowired
	private PricingRepository priceRepo;

	@Autowired
	private SecurityService secSer;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private NotificationRepository notiRepo;

	@Autowired
	private PaymentRepository payRepo;

	@Autowired
	private InvoiceDao invDao;

	public List<Invoice> getMyInvoices(PcUser user) {
		List<Invoice> myInvoices = invRepo.findInvoicesForConsumer(user.getEmail(), user.getMobile());
		return myInvoices;
	}

	public List<Pricing> getPricings() {
		List<Pricing> pricings = priceRepo.findAll();
		return pricings;
	}

	public List<Notification> getNotifications() {
		Pageable topFour = new PageRequest(0, 4);
		if (secSer.isMerchantUser()) {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			List<Notification> notices = notiRepo.findByUserIdAndMerchantIdOrderByIdDesc(null, merchant.getId(),
					topFour);
			return notices;
		} else {
			PcUser user = secSer.findLoggedInUser();
			List<Notification> notices = notiRepo.findByUserIdAndMerchantIdOrderByIdDesc(user.getId(), null, topFour);
			return notices;
		}
	}

	public StatsResponse loadDashboard(StatsRequest request) {
		validateStatsRequest(request);
		StatsResponse response = new StatsResponse();
		SearchInvoiceRequest searchReq = new SearchInvoiceRequest();
		searchReq.setCreatedFrom(request.getCreatedFrom());
		searchReq.setCreatedTo(request.getCreatedTo());
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		searchReq.setInvoiceStatus(InvoiceStatus.UNPAID);
		List<Invoice> unpaidInvs = invDao.findInvoices(searchReq, merchant);
		searchReq.setInvoiceStatus(InvoiceStatus.EXPIRED);
		List<Invoice> expiredInvs = invDao.findInvoices(searchReq, merchant);
		searchReq.setInvoiceStatus(InvoiceStatus.DECLINED);
		List<Invoice> declinedInvs = invDao.findInvoices(searchReq, merchant);

		List<Payment> salePays = new ArrayList<>();
		if (merchant == null) {
			salePays = payRepo.findPaysWithStatus("captured", PayType.SALE, request.getCreatedFrom(),
					request.getCreatedTo());
		} else {
			salePays = payRepo.findPaysWithStatusForMerchant("captured", PayType.SALE, merchant,
					request.getCreatedFrom(), request.getCreatedTo());
		}
		List<Payment> refundPays = new ArrayList<>();
		if (merchant == null) {
			refundPays = payRepo.findPaysWithStatus("refund", PayType.REFUND, request.getCreatedFrom(),
					request.getCreatedTo());
		} else {
			refundPays = payRepo.findPaysWithStatusForMerchant("refund", PayType.REFUND, merchant,
					request.getCreatedFrom(), request.getCreatedTo());
		}

		response.setSalePayCount(salePays.size());
		response.setSalePaySum(getTotalPayAmount(salePays));
		response.setUnpaidInvCount(unpaidInvs.size());
		response.setUnpaidInvSum(getTotalInvAmount(unpaidInvs));
		response.setExpiredInvCount(expiredInvs.size());
		response.setExpiredInvSum(getTotalInvAmount(expiredInvs));
		response.setDeclinedInvCount(declinedInvs.size());
		response.setDeclinedInvSum(getTotalInvAmount(declinedInvs));
		response.setRefundPayCount(refundPays.size());
		response.setRefundPaySum(getTotalPayAmount(refundPays));
		List<DailyPay> dailyPayList = new ArrayList<DailyPay>();
		for (Payment payment : refundPays) {
			setDailyPay(dailyPayList, payment);
		}
		for (Payment payment : salePays) {
			setDailyPay(dailyPayList, payment);
		}
		Collections.sort(dailyPayList);
		response.setDailyPayList(dailyPayList);
		return response;
	}

	private void validateStatsRequest(StatsRequest request) {
		if (request == null || request.getCreatedFrom() == null || request.getCreatedTo() == null) {
			throw new PaycrException(Constants.FAILURE, "Invalid Request");
		}
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(request.getCreatedTo());
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(request.getCreatedFrom());
		calFrom.add(Calendar.DAY_OF_YEAR, 30);
		if (calFrom.before(calTo)) {
			throw new PaycrException(Constants.FAILURE, "Search duration cannot be greater than 30 days");
		}
	}

	private BigDecimal getTotalInvAmount(List<Invoice> invs) {
		BigDecimal total = new BigDecimal(0);
		for (Invoice inv : invs) {
			total = total.add(inv.getPayAmount());
		}
		return total.setScale(2, BigDecimal.ROUND_UP);
	}

	private BigDecimal getTotalPayAmount(List<Payment> pays) {
		BigDecimal total = new BigDecimal(0);
		for (Payment pay : pays) {
			total = total.add(pay.getAmount());
		}
		return total.setScale(2, BigDecimal.ROUND_UP);
	}

	private void setDailyPay(List<DailyPay> dailyPayList, Payment payment) {
		for (DailyPay dp : dailyPayList) {
			if (DateUtil.getDefaultDate(payment.getCreated()).equals(dp.getCreated())) {
				if (PayType.SALE.equals(payment.getPayType())) {
					dp.setSalePaySum(dp.getSalePaySum().add(payment.getAmount()));
				} else if (PayType.REFUND.equals(payment.getPayType())) {
					dp.setRefundPaySum(dp.getRefundPaySum().add(payment.getAmount()));
				}
				return;
			}
		}
		DailyPay dp = new DailyPay();
		dp.setCreated(DateUtil.getDefaultDate(payment.getCreated()));
		if (PayType.SALE.equals(payment.getPayType())) {
			dp.setSalePaySum(payment.getAmount());
			dp.setRefundPaySum(new BigDecimal(0));
		} else if (PayType.REFUND.equals(payment.getPayType())) {
			dp.setRefundPaySum(payment.getAmount());
			dp.setSalePaySum(new BigDecimal(0));
		}
		dailyPayList.add(dp);
	}

}
