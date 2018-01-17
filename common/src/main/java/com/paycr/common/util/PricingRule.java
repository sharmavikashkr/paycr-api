package com.paycr.common.util;

import java.math.BigDecimal;

public class PricingRule {

	public static BigDecimal getPricingRate(int limit, int duration) {
		return BigDecimal.valueOf(((float)limit / duration) * 48.60).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
