package com.paycr.common.util;

import java.math.BigDecimal;

public class PricingRule {

	public static BigDecimal getPricingRate(int limit, int duration) {
		return BigDecimal.valueOf((limit / duration) * 48.6).setScale(2, BigDecimal.ROUND_UP);
	}

}
