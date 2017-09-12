package com.paycr.dashboard.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.DailyPay;
import com.paycr.common.bean.StatsRequest;
import com.paycr.common.bean.StatsResponse;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Notification;
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

	public List<Invoice> getMyInvoices(PcUser user) {
		return invRepo.findInvoicesForConsumer(user.getEmail(), user.getMobile());
	}

	public List<Pricing> getPricings() {
		return priceRepo.findAll();
	}

	public List<Notification> getNotifications() {
		Pageable topFour = new PageRequest(0, 4);
		if (secSer.isMerchantUser()) {
			Merchant merchant = secSer.getMerchantForLoggedInUser();
			return notiRepo.findByUserIdAndMerchantIdOrderByIdDesc(null, merchant.getId(), topFour);
		} else {
			PcUser user = secSer.findLoggedInUser();
			return notiRepo.findByUserIdAndMerchantIdOrderByIdDesc(user.getId(), null, topFour);
		}
	}

	public StatsResponse loadDashboard(StatsRequest request) {
		validateStatsRequest(request);
		StatsResponse response = new StatsResponse();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		List<Object[]> salePays;
		List<Object[]> refundPays;
		List<Object[]> unpaid;
		List<Object[]> expired;
		List<Object[]> declined;
		List<Object[]> dailyPays;
		if (merchant == null) {
			salePays = payRepo.findCountAndSum(PayType.SALE.name(), request.getCreatedFrom(), request.getCreatedTo());
			refundPays = payRepo.findCountAndSum(PayType.REFUND.name(), request.getCreatedFrom(),
					request.getCreatedTo());
			unpaid = invRepo.findCountAndSum(InvoiceStatus.UNPAID.name(), request.getCreatedFrom(),
					request.getCreatedTo());
			expired = invRepo.findCountAndSum(InvoiceStatus.EXPIRED.name(), request.getCreatedFrom(),
					request.getCreatedTo());
			declined = invRepo.findCountAndSum(InvoiceStatus.DECLINED.name(), request.getCreatedFrom(),
					request.getCreatedTo());
			dailyPays = payRepo.findDailyPayList(request.getCreatedFrom(), request.getCreatedTo());
		} else {
			salePays = payRepo.findCountAndSumForMerchant(merchant.getId(), PayType.SALE.name(),
					request.getCreatedFrom(), request.getCreatedTo());
			refundPays = payRepo.findCountAndSumForMerchant(merchant.getId(), PayType.REFUND.name(),
					request.getCreatedFrom(), request.getCreatedTo());
			unpaid = invRepo.findCountAndSumForMerchant(merchant.getId(), InvoiceStatus.UNPAID.name(),
					request.getCreatedFrom(), request.getCreatedTo());
			expired = invRepo.findCountAndSumForMerchant(merchant.getId(), InvoiceStatus.EXPIRED.name(),
					request.getCreatedFrom(), request.getCreatedTo());
			declined = invRepo.findCountAndSumForMerchant(merchant.getId(), InvoiceStatus.DECLINED.name(),
					request.getCreatedFrom(), request.getCreatedTo());
			dailyPays = payRepo.findDailyPayListForMerchant(request.getCreatedFrom(), request.getCreatedTo(),
					merchant.getId());
		}
		response.setSalePayCount((BigInteger) salePays.get(0)[0]);
		response.setSalePaySum(BigDecimal.valueOf((Double) salePays.get(0)[1]).setScale(2, BigDecimal.ROUND_FLOOR));
		response.setRefundPayCount((BigInteger) refundPays.get(0)[0]);
		response.setRefundPaySum(BigDecimal.valueOf((Double) refundPays.get(0)[1]).setScale(2, BigDecimal.ROUND_FLOOR));
		response.setUnpaidInvCount((BigInteger) unpaid.get(0)[0]);
		response.setUnpaidInvSum(BigDecimal.valueOf((Double) unpaid.get(0)[1] == null ? 0D : (Double) unpaid.get(0)[1]).setScale(2, BigDecimal.ROUND_FLOOR));
		response.setExpiredInvCount((BigInteger) expired.get(0)[0]);
		response.setExpiredInvSum(BigDecimal.valueOf((Double) expired.get(0)[1] == null ? 0D : (Double) expired.get(0)[1]).setScale(2, BigDecimal.ROUND_FLOOR));
		response.setDeclinedInvCount((BigInteger) declined.get(0)[0]);
		response.setDeclinedInvSum(BigDecimal.valueOf((Double) declined.get(0)[1] == null ? 0D : (Double) declined.get(0)[1]).setScale(2, BigDecimal.ROUND_FLOOR));
		List<DailyPay> dailyPayList = new ArrayList<>();
		for (Object[] obj : dailyPays) {
			DailyPay dp = new DailyPay();
			dp.setCreated((String) obj[0]);
			dp.setSalePaySum((Double) obj[1]);
			dp.setRefundPaySum((Double) obj[2]);
			dailyPayList.add(dp);
		}
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

}
