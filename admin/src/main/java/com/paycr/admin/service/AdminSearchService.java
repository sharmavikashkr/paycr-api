package com.paycr.admin.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.SearchMerchantRequest;
import com.paycr.common.bean.SearchPromotionRequest;
import com.paycr.common.bean.SearchSubsRequest;
import com.paycr.common.data.dao.MerchantDao;
import com.paycr.common.data.dao.PromotionDao;
import com.paycr.common.data.dao.SubscriptionDao;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.Promotion;
import com.paycr.common.data.domain.Subscription;
import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;
import com.paycr.common.util.DateUtil;

@Service
public class AdminSearchService {

	@Autowired
	private MerchantDao merDao;

	@Autowired
	private SubscriptionDao subsDao;

	@Autowired
	private PromotionDao promoDao;

	public List<Merchant> fetchMerchantList(SearchMerchantRequest request) {
		vaidateRequest(request);
		validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		return merDao.findMerchants(request);
	}

	public List<Subscription> fetchSubsList(SearchSubsRequest request) {
		vaidateRequest(request);
		validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		return subsDao.findSubscriptions(request);
	}

	public List<Promotion> fetchPromotionList(SearchPromotionRequest request) {
		vaidateRequest(request);
		validateDates(request.getCreatedFrom(), request.getCreatedTo());
		request.setCreatedFrom(
				DateUtil.getISTTimeInUTC(DateUtil.getStartOfDay(DateUtil.getUTCTimeInIST(request.getCreatedFrom()))));
		request.setCreatedTo(
				DateUtil.getISTTimeInUTC(DateUtil.getEndOfDay(DateUtil.getUTCTimeInIST(request.getCreatedTo()))));
		return promoDao.findPromotions(request);
	}

	private void vaidateRequest(Object request) {
		if (request == null) {
			throw new PaycrException(Constants.FAILURE, "Mandatory params missing");
		}
	}

	private void validateDates(Date from, Date to) {
		if (from == null || to == null) {
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
