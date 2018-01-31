package com.paycr.common.util;

import java.util.Calendar;
import java.util.Date;

import com.paycr.common.exception.PaycrException;

public class PaycrUtil {

	public static void validateRequest(Object request) {
		if (CommonUtil.isNull(request)) {
			throw new PaycrException(Constants.FAILURE, "Mandatory params missing");
		}
	}

	public static void validateDates(Date from, Date to) {
		if (CommonUtil.isNull(from) || CommonUtil.isNull(to)) {
			throw new PaycrException(Constants.FAILURE, "From/To dates cannot be null");
		}
		from = DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(from)));
		to = DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(to)));
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(to);
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(from);
		calFrom.add(Calendar.DAY_OF_YEAR, 90);
		if (calFrom.before(calTo)) {
			throw new PaycrException(Constants.FAILURE, "Search duration cannot be greater than 90 days");
		}
	}

}
