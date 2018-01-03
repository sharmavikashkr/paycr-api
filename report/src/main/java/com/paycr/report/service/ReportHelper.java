package com.paycr.report.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.paycr.common.bean.DateFilter;
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.DateUtil;

@Component
public class ReportHelper {

	public DateFilter getDateFilter(TimeRange range) {
		DateFilter dateFilter = null;
		Calendar calendar = Calendar.getInstance();
		if (TimeRange.YESTERDAY.equals(range)) {
			Date aTimeYesterday = DateUtil.addDays(calendar.getTime(), -1);
			Date start = DateUtil.getStartOfDay(aTimeYesterday);
			Date end = DateUtil.getEndOfDay(aTimeYesterday);
			dateFilter = new DateFilter(start, end);
		} else if (TimeRange.LAST_WEEK.equals(range)) {
			Date aDayInLastWeek = DateUtil.addDays(calendar.getTime(), -7);
			Date start = DateUtil.getFirstDayOfWeek(aDayInLastWeek);
			Date end = DateUtil.getLastDayOfWeek(aDayInLastWeek);
			dateFilter = new DateFilter(start, end);
		} else if (TimeRange.LAST_MONTH.equals(range)) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			Date aDayInLastMonth = DateUtil.addDays(calendar.getTime(), -20);
			Date start = DateUtil.getFirstDayOfMonth(aDayInLastMonth);
			Date end = DateUtil.getLastDayOfMonth(aDayInLastMonth);
			dateFilter = new DateFilter(start, end);
		} else if (TimeRange.LAST_YEAR.equals(range)) {
			calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			Date aDayInLastYear = DateUtil.addDays(calendar.getTime(), -10);
			Date start = DateUtil.getFirstDayOfYear(aDayInLastYear);
			Date end = DateUtil.getLastDayOfYear(aDayInLastYear);
			dateFilter = new DateFilter(start, end);
		}
		return dateFilter;
	}

}
