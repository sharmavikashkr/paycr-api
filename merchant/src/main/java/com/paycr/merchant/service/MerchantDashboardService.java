package com.paycr.merchant.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.DailyPay;
import com.paycr.common.bean.StatsResponse;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.ExpensePaymentRepository;
import com.paycr.common.data.repository.InvoicePaymentRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.service.SecurityService;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.PayType;

@Service
public class MerchantDashboardService {

	@Autowired
	private SecurityService secSer;

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private InvoicePaymentRepository invPayRepo;

	@Autowired
	private ExpensePaymentRepository expPayRepo;

	public StatsResponse loadDashboard(String timeRange) {
		Calendar calTo = Calendar.getInstance();
		Calendar calFrom = Calendar.getInstance();
		if ("LAST_WEEK".equalsIgnoreCase(timeRange)) {
			calFrom.add(Calendar.DATE, -7);
		} else if ("LAST_MONTH".equalsIgnoreCase(timeRange)) {
			calFrom.add(Calendar.DATE, -30);
		} else if ("LAST_2WEEK".equalsIgnoreCase(timeRange)) {
			calFrom.add(Calendar.DATE, -14);
		}
		StatsResponse response = new StatsResponse();
		Merchant merchant = secSer.getMerchantForLoggedInUser();
		List<Object[]> salePays;
		List<Object[]> refundPays;
		List<Object[]> unpaid;
		List<Object[]> expired;
		List<Object[]> declined;
		List<Object[]> dailyPays;
		salePays = invPayRepo.findCountAndSumForMerchant(merchant.getId(), PayType.SALE.name(), calFrom.getTime(),
				calTo.getTime());
		refundPays = invPayRepo.findCountAndSumForMerchant(merchant.getId(), PayType.REFUND.name(), calFrom.getTime(),
				calTo.getTime());
		unpaid = invRepo.findCountAndSumForMerchant(merchant.getId(), InvoiceStatus.UNPAID.name(), calFrom.getTime(),
				calTo.getTime());
		expired = invRepo.findCountAndSumForMerchant(merchant.getId(), InvoiceStatus.EXPIRED.name(), calFrom.getTime(),
				calTo.getTime());
		declined = invRepo.findCountAndSumForMerchant(merchant.getId(), InvoiceStatus.DECLINED.name(),
				calFrom.getTime(), calTo.getTime());
		dailyPays = invPayRepo.findDailyPayListForMerchant(calFrom.getTime(), calTo.getTime(), merchant.getId());

		response.setSaleInvPayCount((BigInteger) salePays.get(0)[0]);
		response.setSaleInvPaySum(
				BigDecimal.valueOf((Double) salePays.get(0)[1] == null ? 0D : (Double) salePays.get(0)[1]).setScale(2,
						BigDecimal.ROUND_FLOOR));
		response.setRefundInvPayCount((BigInteger) refundPays.get(0)[0]);
		response.setRefundInvPaySum(
				BigDecimal.valueOf((Double) refundPays.get(0)[1] == null ? 0D : (Double) refundPays.get(0)[1])
						.setScale(2, BigDecimal.ROUND_FLOOR));
		response.setUnpaidInvCount((BigInteger) unpaid.get(0)[0]);
		response.setUnpaidInvSum(BigDecimal.valueOf((Double) unpaid.get(0)[1] == null ? 0D : (Double) unpaid.get(0)[1])
				.setScale(2, BigDecimal.ROUND_FLOOR));
		response.setExpiredInvCount((BigInteger) expired.get(0)[0]);
		response.setExpiredInvSum(
				BigDecimal.valueOf((Double) expired.get(0)[1] == null ? 0D : (Double) expired.get(0)[1]).setScale(2,
						BigDecimal.ROUND_FLOOR));
		response.setDeclinedInvCount((BigInteger) declined.get(0)[0]);
		response.setDeclinedInvSum(
				BigDecimal.valueOf((Double) declined.get(0)[1] == null ? 0D : (Double) declined.get(0)[1]).setScale(2,
						BigDecimal.ROUND_FLOOR));
		List<DailyPay> dailyInvPayList = new ArrayList<>();
		for (Object[] obj : dailyPays) {
			DailyPay dp = new DailyPay();
			dp.setCreated((String) obj[0]);
			dp.setSalePaySum((Double) obj[1]);
			dp.setRefundPaySum((Double) obj[2]);
			dailyInvPayList.add(dp);
		}
		response.setDailyInvPayList(dailyInvPayList);

		dailyPays.clear();

		salePays = expPayRepo.findCountAndSumForMerchant(merchant.getId(), PayType.SALE.name(), calFrom.getTime(),
				calTo.getTime());
		refundPays = expPayRepo.findCountAndSumForMerchant(merchant.getId(), PayType.REFUND.name(), calFrom.getTime(),
				calTo.getTime());
		unpaid = expPayRepo.findCountAndSumForMerchant(merchant.getId(), ExpenseStatus.UNPAID.name(), calFrom.getTime(),
				calTo.getTime());
		dailyPays = expPayRepo.findDailyPayListForMerchant(calFrom.getTime(), calTo.getTime(), merchant.getId());

		response.setSaleExpPayCount((BigInteger) salePays.get(0)[0]);
		response.setSaleExpPaySum(
				BigDecimal.valueOf((Double) salePays.get(0)[1] == null ? 0D : (Double) salePays.get(0)[1]).setScale(2,
						BigDecimal.ROUND_FLOOR));
		response.setRefundExpPayCount((BigInteger) refundPays.get(0)[0]);
		response.setRefundExpPaySum(
				BigDecimal.valueOf((Double) refundPays.get(0)[1] == null ? 0D : (Double) refundPays.get(0)[1])
						.setScale(2, BigDecimal.ROUND_FLOOR));
		response.setUnpaidExpCount((BigInteger) unpaid.get(0)[0]);
		response.setUnpaidExpSum(BigDecimal.valueOf((Double) unpaid.get(0)[1] == null ? 0D : (Double) unpaid.get(0)[1])
				.setScale(2, BigDecimal.ROUND_FLOOR));

		List<DailyPay> dailyExpPayList = new ArrayList<>();
		for (Object[] obj : dailyPays) {
			DailyPay dp = new DailyPay();
			dp.setCreated((String) obj[0]);
			dp.setSalePaySum((Double) obj[1]);
			dp.setRefundPaySum((Double) obj[2]);
			dailyExpPayList.add(dp);
		}
		response.setDailyExpPayList(dailyExpPayList);

		return response;
	}

}
