package com.paycr.common.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import lombok.Data;

@Data
public class StatsResponse {

	private BigDecimal saleInvPaySum;
	private BigDecimal unpaidInvSum;
	private BigDecimal declinedInvSum;
	private BigDecimal expiredInvSum;
	private BigDecimal refundInvPaySum;
	private BigInteger saleInvPayCount;
	private BigInteger unpaidInvCount;
	private BigInteger declinedInvCount;
	private BigInteger expiredInvCount;
	private BigInteger refundInvPayCount;
	private List<DailyPay> dailyInvPayList;

	private BigDecimal saleExpPaySum;
	private BigDecimal unpaidExpSum;
	private BigDecimal refundExpPaySum;
	private BigInteger saleExpPayCount;
	private BigInteger unpaidExpCount;
	private BigInteger refundExpPayCount;
	private List<DailyPay> dailyExpPayList;

}
