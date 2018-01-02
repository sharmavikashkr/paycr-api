package com.paycr.admin.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.DailyPay;
import com.paycr.common.bean.StatsResponse;
import com.paycr.common.data.repository.InvoicePaymentRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.type.PayType;

@Service
public class AdminDashboardService {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private InvoicePaymentRepository payRepo;

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
		List<Object[]> salePays;
		List<Object[]> refundPays;
		List<Object[]> unpaid;
		List<Object[]> expired;
		List<Object[]> declined;
		List<Object[]> dailyPays;
		salePays = payRepo.findCountAndSum(PayType.SALE.name(), calFrom.getTime(), calTo.getTime());
		refundPays = payRepo.findCountAndSum(PayType.REFUND.name(), calFrom.getTime(), calTo.getTime());
		unpaid = invRepo.findCountAndSum(InvoiceStatus.UNPAID.name(), calFrom.getTime(), calTo.getTime());
		expired = invRepo.findCountAndSum(InvoiceStatus.EXPIRED.name(), calFrom.getTime(), calTo.getTime());
		declined = invRepo.findCountAndSum(InvoiceStatus.DECLINED.name(), calFrom.getTime(), calTo.getTime());
		dailyPays = payRepo.findDailyPayList(calFrom.getTime(), calTo.getTime());

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
		List<DailyPay> dailyPayList = new ArrayList<>();
		for (Object[] obj : dailyPays) {
			DailyPay dp = new DailyPay();
			dp.setCreated((String) obj[0]);
			dp.setSalePaySum((Double) obj[1]);
			dp.setRefundPaySum((Double) obj[2]);
			dailyPayList.add(dp);
		}
		response.setDailyInvPayList(dailyPayList);
		return response;
	}

}
