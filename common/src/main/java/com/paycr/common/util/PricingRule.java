package com.paycr.common.util;

import java.math.BigDecimal;

public class PricingRule {

	public static BigDecimal getPricingRate(int limit, int duration) {
		return BigDecimal.valueOf((limit / duration) * 10L).setScale(2, BigDecimal.ROUND_UP);
	}

}
